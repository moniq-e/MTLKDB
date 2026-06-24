package com.mtlk.mtlkdb.core.persistence.index;

import java.io.IOException;
import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.core.persistence.record.DiskManager;
import com.mtlk.mtlkdb.dto.SplitDTO;
import com.mtlk.mtlkdb.struct.IndexPageType;
import com.mtlk.mtlkdb.struct.RecordId;
import com.mtlk.mtlkdb.struct.util.ByteArray;

public class IndexManager {
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
        var biggestKey = fromKey;
        var res = new ArrayList<RecordId>();
        while (true) { //TODO
            var rids = actual.getRecordIds(biggestKey, toKey);
            res.addAll(rids.recordIds());
            biggestKey = rids.lastKey();

            if (biggestKey >= toKey) break;

            actual = IndexLeafPage.deserialize(indexDM.readPage(actual.getNextPageId()));
        }
        return res.toArray(RecordId[]::new);
    }

    public void insert(int key, RecordId rid) throws IOException {
        int rootPageId = header.getRootPageId();

        var dto = insertAndSplit(rootPageId, key, rid);

        if (dto != null) {
            int newRootPageId = header.getNextFreePageId();
            var newRoot = new IndexInternalPage(rootPageId);

            newRoot.insertChildPageId(dto.promotedKey(), dto.newPageId());

            indexDM.writePage(newRootPageId, newRoot.serialize());
            header.setRootPageId(newRootPageId);
            header.incNextFreePageId();
        }

        indexDM.writePage(0, header.serialize());
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

                indexDM.writePage(pageId, ByteArray.EMPTY_PAGE);
                indexDM.writePage(leaf.getNextPageId(), nextLeaf.serialize());
                indexDM.writePage(leaf.getPreviousPageId(), prevLeaf.serialize());
                return true;
            }

            indexDM.writePage(pageId, leaf.serialize());
            return false;
        }

        var internal = IndexInternalPage.deserialize(pageData);
        var childPageId = internal.getChildPageId(key);

        var childIsEmpty = removeAndImplodes(childPageId, key);

        if (childIsEmpty) {
            internal.removeChildPageId(key);

            if (internal.isEmpty()) {
                indexDM.writePage(pageId, ByteArray.EMPTY_PAGE);
                return true;
            }
            indexDM.writePage(pageId, internal.serialize());
        }
        return false;
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

            indexDM.writePage(pageId, leaf.serialize());
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

            indexDM.writePage(pageId, internal.serialize());
        }
        return null;
    }

    private SplitDTO splitAndSave(AbstractIndexPage page, int pageId) throws IOException {
        int newPageId = header.getNextFreePageId();

        int promotedKey = page.getPromotionKey();

        var newPage = page.split(pageId, newPageId);

        indexDM.writePage(pageId, page.serialize());
        indexDM.writePage(newPageId, newPage.serialize());
        header.incNextFreePageId();

        return new SplitDTO(promotedKey, newPageId);
    }

    private int findChildPageIdInInternalNode(byte[] internalPageData, int key) {
        var internalPage = IndexInternalPage.deserialize(internalPageData);
        return internalPage.getChildPageId(key);
    }
}
