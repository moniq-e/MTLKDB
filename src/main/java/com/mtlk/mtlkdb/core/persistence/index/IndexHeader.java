package com.mtlk.mtlkdb.core.persistence.index;

import static com.mtlk.mtlkdb.core.persistence.index.IndexManager.PAGE_SIZE;

import com.mtlk.mtlkdb.core.persistence.SerializablePage;
import com.mtlk.mtlkdb.struct.util.ByteArray;

public class IndexHeader implements SerializablePage {
    private static final byte HEADER_BYTE = 'M';

    private int rootPageId;
    private int nextFreePageId;

    private IndexHeader() {
        rootPageId = 1;
        nextFreePageId = 2;
    }

    public static IndexHeader deserialize(byte[] data) {
        var buffer = new ByteArray(data);
        var header = new IndexHeader();

        var hb = buffer.get();

        if (hb == HEADER_BYTE) {
            header.setRootPageId(buffer.getInt());
            header.setNextFreePageId(buffer.getInt());
        }

        return header;
    }

    @Override
    public byte[] serialize() {
        var buffer = ByteArray.allocate(PAGE_SIZE);

        buffer.put(HEADER_BYTE);
        buffer.putInt(getRootPageId());
        buffer.putInt(getNextFreePageId());

        return buffer.toArray();
    }

    public int getRootPageId() {
        return rootPageId;
    }

    public void setRootPageId(int rootPageId) {
        this.rootPageId = rootPageId;
    }

    public int getNextFreePageId() {
        return nextFreePageId;
    }
    
    public void setNextFreePageId(int nextFreePageId) {
        this.nextFreePageId = nextFreePageId;
    }

    public void incNextFreePageId() {
        nextFreePageId++;
    }
}
