package com.mtlk.mtlkdb.core.persistence.index;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.core.persistence.DiskManager;
import com.mtlk.mtlkdb.dto.SplitDTO;
import com.mtlk.mtlkdb.struct.IndexPageType;
import com.mtlk.mtlkdb.struct.RecordId;
import com.mtlk.mtlkdb.struct.util.ByteArray;

public class IndexManager implements Closeable {
    public static final int PAGE_SIZE = 4096;

    private DiskManager indexDM;
    private IndexHeader header;

    public IndexManager(String indexFileName) throws IOException {
        this.indexDM = new DiskManager(indexFileName);
        this.header = IndexHeader.deserialize(indexDM.readPage(0));
    }

    @Nullable
    public RecordId search(int key) throws IOException {
        int currentPageId = header.getRootPageId();

        var pageData = indexDM.readPage(currentPageId);

        while (pageData[0] == IndexPageType.INTERNAL.get()) { 
            currentPageId = findChildPageIdInInternalNode(pageData, key);
            pageData = indexDM.readPage(currentPageId);
        }

        var leaf = IndexLeafPage.deserialize(pageData);

        return leaf.getRecordIdByKey(key);
    }

    public RecordId[] search(int fromKey, int toKey) throws IOException {
        int currentPageId = header.getRootPageId();

        var pageData = indexDM.readPage(currentPageId);

        while (pageData[0] == IndexPageType.INTERNAL.get()) { 
            currentPageId = findChildPageIdInInternalNode(pageData, fromKey);
            pageData = indexDM.readPage(currentPageId);
        }

        var actual = IndexLeafPage.deserialize(pageData);
        var currentFromKey = fromKey;
        var res = new ArrayList<RecordId>();

        while (true) {
            var ridto = actual.getRecordIds(currentFromKey, toKey);

            if (ridto.recordIds() != null) {
                res.addAll(ridto.recordIds());
                currentFromKey = ridto.lastKey() + 1;
            }

            if (!ridto.goToNextPage()) break;

            var nextPageId = actual.getNextPageId();
            if (nextPageId == -1) break;

            actual = IndexLeafPage.deserialize(indexDM.readPage(nextPageId));
        }
        return res.toArray(RecordId[]::new);
    }

    public void insert(int key, RecordId rid) throws IOException {
        int rootPageId = header.getRootPageId();

        var dto = insertAndSplit(rootPageId, key, rid);

        if (dto != null) {
            int newRootPageId = allocatePage();
            var newRoot = new IndexInternalPage(rootPageId);

            newRoot.insertChildPageId(dto.promotedKey(), dto.newPageId());

            indexDM.writePage(newRootPageId, newRoot);
            header.setRootPageId(newRootPageId);
        }

        indexDM.writePage(0, header);
    }

    @Nullable
    private SplitDTO insertAndSplit(int pageId, int key, RecordId rid) throws IOException {
        var pageData = indexDM.readPage(pageId);

        if (pageData[0] == IndexPageType.LEAF.get()) {
            var leaf = IndexLeafPage.deserialize(pageData);
            leaf.insert(key, rid);

            if (leaf.isFull()) {
                return splitAndSave(leaf, pageId);
            }

            indexDM.writePage(pageId, leaf);
            return null;
        }

        var internal = IndexInternalPage.deserialize(pageData);
        int childPageId = internal.getChildPageId(key);

        var dto = insertAndSplit(childPageId, key, rid);

        if (dto != null) {
            internal.insertChildPageId(dto.promotedKey(), dto.newPageId());

            if (internal.isFull()) {
                return splitAndSave(internal, pageId);
            }

            indexDM.writePage(pageId, internal);
        }
        return null;
    }

    private SplitDTO splitAndSave(AbstractIndexPage page, int pageId) throws IOException {
        int newPageId = allocatePage();

        int promotedKey = page.getPromotionKey();

        var newPage = page.split(pageId, newPageId);

        indexDM.writePage(pageId, page);
        indexDM.writePage(newPageId, newPage);

        return new SplitDTO(promotedKey, newPageId);
    }

    public void remove(int key) throws IOException {
        int rootPageId = header.getRootPageId();

        removeAndImplodes(rootPageId, key);
    }

    private boolean removeAndImplodes(int pageId, int key) throws IOException {
        var pageData = indexDM.readPage(pageId);

        if (pageData[0] == IndexPageType.LEAF.get()) {
            var leaf = IndexLeafPage.deserialize(pageData);
            leaf.remove(key);

            if (leaf.isEmpty()) {
                var prevLeaf = IndexLeafPage.deserialize(indexDM.readPage(leaf.getPreviousPageId()));
                var nextLeaf = IndexLeafPage.deserialize(indexDM.readPage(leaf.getNextPageId()));

                prevLeaf.setNextPageId(leaf.getNextPageId());
                nextLeaf.setPreviousPageId(leaf.getPreviousPageId());

                freePage(pageId);
                indexDM.writePage(leaf.getNextPageId(), nextLeaf);
                indexDM.writePage(leaf.getPreviousPageId(), prevLeaf);
                return true;
            }

            indexDM.writePage(pageId, leaf);
            return false;
        }

        var internal = IndexInternalPage.deserialize(pageData);
        var childPageId = internal.getChildPageId(key);

        var childIsEmpty = removeAndImplodes(childPageId, key);

        if (childIsEmpty) {
            internal.removeChildPageId(key);

            if (internal.isEmpty()) {
                freePage(pageId);
                return true;
            }
            indexDM.writePage(pageId, internal);
        }
        return false;
    }

    private int findChildPageIdInInternalNode(byte[] internalPageData, int key) {
        var internalPage = IndexInternalPage.deserialize(internalPageData);
        return internalPage.getChildPageId(key);
    }

    private void freePage(int pageId) throws IOException {
        var bytes = ByteArray.allocate(PAGE_SIZE);

        bytes.putInt(header.getFreePageHead());
        header.setFreePageHead(pageId);

        indexDM.writePage(pageId, bytes.toArray());
    }

    private int allocatePage() throws IOException {
        var freePageId = header.getFreePageHead();

        if (freePageId <= -1) {
            freePageId = header.getNextFreePageId();
            header.incNextFreePageId();
            return freePageId;
        }

        var bytes = new ByteArray(indexDM.readPage(freePageId));
        header.setNextFreePageId(bytes.getInt());

        return freePageId;
    }

    @Override
    public void close() throws IOException {
        indexDM.writePage(0, header);
        indexDM.close();
    }
}
