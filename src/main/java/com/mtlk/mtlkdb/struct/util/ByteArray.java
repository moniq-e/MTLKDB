package com.mtlk.mtlkdb.struct.util;

import java.util.Arrays;

import com.mtlk.mtlkdb.core.persistence.record.DiskManager;

public class ByteArray {
    public static final byte[] EMPTY_PAGE = new byte[DiskManager.PAGE_SIZE];

    private byte[] byteArr;
    private int idx;
    private boolean isOpen;

    public ByteArray(byte[] byteArr) {
        this.byteArr = byteArr;
        idx = 0;
        isOpen = true;
    }

    public static ByteArray allocate(int size) {
        return new ByteArray(new byte[size]);
    }

    public void put(byte[] value, int index) {
        checkOpen();

        var bIdx = idx;
        System.arraycopy(value, 0, byteArr, index, value.length);
        idx = bIdx;
    }

    public void put(byte value) {
        checkOpen();

        byteArr[idx] = value;
        idx++;
    }
    
    public void put(byte[] value) {
        checkOpen();

        System.arraycopy(value, 0, byteArr, idx, value.length);
        idx += value.length;
    }
    
    public void putInt(int value) {
        checkOpen();

        System.arraycopy(Encoder.encodeInt(value), 0, byteArr, idx, 4);
        idx += 4;
    }

    public void putShort(int value) {
        checkOpen();

        System.arraycopy(Encoder.encodeShort(value), 0, byteArr, idx, 2);
        idx += 2;
    }

    public byte get() {
        checkOpen();

        return byteArr[idx++];
    }

    public int getInt() {
        checkOpen();

        var res = Encoder.decodeInt(Arrays.copyOfRange(byteArr, idx, idx + 4));
        idx += 4;
        return res;
    }

    public byte get(int index) {
        return byteArr[index];
    }

    public int getInt(int index) {
        return Encoder.decodeInt(Arrays.copyOfRange(byteArr, index, index + 4));
    }

    public byte[] get(int index, int length) {
        return Arrays.copyOfRange(byteArr, index, length);
    }

    public byte[] toArray() {
        isOpen = false;

        return byteArr;
    }

    private void checkOpen() {
        if (!isOpen) throw new IllegalStateException("Cannot operate under closed ByteArray.");
    }
}
