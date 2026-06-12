package com.mtlk.mtlkdb.core.persistence;

import java.io.IOException;

import com.mtlk.mtlkdb.core.persistence.page.IndexInternalPage;
import com.mtlk.mtlkdb.core.persistence.page.IndexLeafPage;
import com.mtlk.mtlkdb.struct.IndexPageType;
import com.mtlk.mtlkdb.struct.RecordId;

public class IndexManager {
    public static final int PAGE_SIZE = 4096;

    private DiskManager indexDM;
    private IndexHeader header;

    public IndexManager(String indexFileName) throws IOException {
        this.indexDM = new DiskManager(indexFileName);
        this.header = IndexHeader.deserialize(indexDM.readPage(0));
    }

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

    public void insert(int key, RecordId rid) throws IOException {
        int currentPageId = header.getRootPageId();

        var pageData = indexDM.readPage(currentPageId);

        while (pageData[0] == IndexPageType.INTERNAL.get()) { 
            currentPageId = findChildPageIdInInternalNode(pageData, key);
            pageData = indexDM.readPage(currentPageId);
        }

        var leaf = IndexLeafPage.deserialize(pageData);

        leaf.insertRecordId(key, rid);

        if (leaf.isFull()) {
            split();
        }
    }

    private void split() throws IOException {
        split(header.getRootPageId());
        indexDM.writePage(0, header.serialize());
    }

    private void split(int pageId) throws IOException {
        var pageData = indexDM.readPage(pageId);

        if (pageData[0] == IndexPageType.LEAF.get()) {
            var leaf = IndexLeafPage.deserialize(pageData);
            if (!leaf.isFull()) return;

            var newLeaf = leaf.createSplittedCopy();
            leaf.split(header.getNextFreePageId());
            
            indexDM.writePage(pageId, leaf.serialize());
            indexDM.writePage(header.getNextFreePageId(), newLeaf.serialize());
            header.incNextFreePageId();
        } else if (pageData[0] == IndexPageType.INTERNAL.get()) {
            
        }
    }

    private int findChildPageIdInInternalNode(byte[] internalPageData, int key) {
        var internalPage = IndexInternalPage.deserialize(internalPageData);
        return internalPage.getChildPageId(key);
    }
}
