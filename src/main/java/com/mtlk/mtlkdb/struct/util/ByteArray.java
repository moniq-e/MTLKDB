package com.mtlk.mtlkdb.struct.util;

import java.util.Arrays;

public class ByteArray {
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

    public void put(byte value) {
        if (!isOpen) throw new IllegalStateException("Cannot operate under closed ByteArray.");

        byteArr[idx] = value;
        idx++;
    }
    
    public void put(byte[] value) {
        if (!isOpen) throw new IllegalStateException("Cannot operate under closed ByteArray.");

        System.arraycopy(value, 0, byteArr, idx, value.length);
        idx += value.length;
    }
    
    public void putInt(int value) {
        if (!isOpen) throw new IllegalStateException("Cannot operate under closed ByteArray.");

        System.arraycopy(Encoder.encodeInt(value), 0, byteArr, idx, 4);
        idx += 4;
    }

    public byte get() {
        if (!isOpen) throw new IllegalStateException("Cannot operate under closed ByteArray.");

        return byteArr[idx++];
    }

    public int getInt() {
        if (!isOpen) throw new IllegalStateException("Cannot operate under closed ByteArray.");

        return Encoder.decodeInt(Arrays.copyOfRange(byteArr, idx++, 4));
    }

    public byte get(int index) {
        return byteArr[index];
    }

    public int getInt(int index) {
        return Encoder.decodeInt(Arrays.copyOfRange(byteArr, index, 4));
    }

    public byte[] get(int index, int length) {
        return Arrays.copyOfRange(byteArr, index, length);
    }

    public byte[] toArray() {
        isOpen = false;

        return byteArr;
    }
}
