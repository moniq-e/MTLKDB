package com.mtlk.mtlkdb.core.persistence.record;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DiskManager {
    public static final int PAGE_SIZE = 4096;
    private RandomAccessFile dbFile;

    public DiskManager(String fileName) throws IOException {
        dbFile = new RandomAccessFile("./" + fileName, "rw");
    }

    public byte[] readPage(int pageId) throws IOException {
        byte[] pageData = new byte[PAGE_SIZE];
        long offset = (long) pageId * PAGE_SIZE;

        dbFile.seek(offset);
        dbFile.read(pageData);

        return pageData;
    }

    public void writePage(int pageId, byte[] pageData) throws IOException {
        if (pageData.length != PAGE_SIZE) {
            throw new IllegalArgumentException("A página deve ter exatamente "+PAGE_SIZE+" bytes.");
        }
        long offset = (long) pageId * PAGE_SIZE;

        dbFile.seek(offset);
        dbFile.write(pageData);
    }
}
