package com.mtlk.mtlkdb.core.persistence;

import java.io.IOException;

import com.mtlk.mtlkdb.core.persistence.page.IndexInternalPage;
import com.mtlk.mtlkdb.core.persistence.page.IndexLeafPage;
import com.mtlk.mtlkdb.struct.RecordId;

public class IndexManager {
    public static final int PAGE_SIZE = 4096;

    private DiskManager indexDM;
    private IndexHeader header;

    public IndexManager(String indexFileName) throws IOException {
        this.indexDM = new DiskManager(indexFileName);
        this.header = IndexHeader.deserialize(indexDM.readPage(0));
    }

    public RecordId search(int key) throws Exception {
        int currentPageId = header.getRootPageId();

        byte[] pageData = indexDM.readPage(currentPageId);

        while (pageData[0] == 0) { 
            currentPageId = findChildPageIdInInternalNode(pageData, key);
            pageData = indexDM.readPage(currentPageId);
        }

        var leaf = IndexLeafPage.deserialize(pageData);

        return leaf.getRecordIdByKey(key);
    }

    private int findChildPageIdInInternalNode(byte[] internalPageData, int key) {
        var internalPage = IndexInternalPage.deserialize(internalPageData);
        return internalPage.getChildPageId(key);
    }
}
