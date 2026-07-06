package com.mtlk.mtlkdb.core.persistence;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class DiskManager implements Closeable {
    public static final int PAGE_SIZE = 4096;
    private RandomAccessFile dbFile;

    public DiskManager(Path path) throws IOException {
        dbFile = new RandomAccessFile(path.toString(), "rw");
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

    public void writePage(int pageId, SerializablePage page) throws IOException {
        writePage(pageId, page.serialize());
    }

    public long getFileSize() {
        try {
            return dbFile.length();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void close() throws IOException {
        dbFile.close();
    }
}
