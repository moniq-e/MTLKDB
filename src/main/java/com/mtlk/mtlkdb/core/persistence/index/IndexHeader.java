package com.mtlk.mtlkdb.core.persistence.index;

import static com.mtlk.mtlkdb.core.persistence.index.IndexManager.PAGE_SIZE;

import com.mtlk.mtlkdb.struct.util.ByteArray;

public class IndexHeader {
    private int rootPageId;
    private int nextFreePageId;

    private IndexHeader() {}

    public static IndexHeader deserialize(byte[] data) {
        var buffer = new ByteArray(data);
        var header = new IndexHeader();

        header.rootPageId = buffer.getInt(); //TODO
        header.nextFreePageId = buffer.getInt();

        if (header.rootPageId == 0) header.setRootPageId(1);
        if (header.nextFreePageId == 0) header.setNextFreePageId(2);

        return header;
    }

    public byte[] serialize() {
        var buffer = ByteArray.allocate(PAGE_SIZE);

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
