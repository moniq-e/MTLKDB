package com.mtlk.mtlkdb.struct.util;

import java.util.Arrays;

import com.mtlk.mtlkdb.core.persistence.DiskManager;
import com.mtlk.mtlkdb.struct.encoder.AbstractByteArray;
import com.mtlk.mtlkdb.struct.encoder.Encoder;

public class ByteBufferMan<T extends AbstractByteArray> {
    public static final byte[] EMPTY_PAGE = new byte[DiskManager.PAGE_SIZE];

    private byte[] byteArr;
    private int idx;
    private boolean isOpen;
    private Encoder<T> encoder;

    private ByteBufferMan(byte[] byteArr, Encoder<T> encoder) {
        this.byteArr = byteArr;
        this.encoder = encoder;
        idx = 0;
        isOpen = true;
    }

    public ByteBufferMan(T byteArr) {
        this(byteArr.value(), Encoder.getEncoder(byteArr));
    }

    public static <T extends AbstractByteArray> ByteBufferMan<T> allocate(int size, Encoder<T> encoder) {
        return new ByteBufferMan<>(new byte[size], encoder);
    }

    public void put(T value, int index) {
        put(value.value(), index);
    }

    public void put(byte[] value, int index) {
        checkOpen();

        System.arraycopy(value, 0, byteArr, index, value.length);
    }

    public void put(byte value) {
        checkOpen();

        byteArr[idx] = value;
        idx++;
    }
    
    public void put(T value) {
        put(value.value());
    }

    public void put(byte[] value) {
        checkOpen();

        System.arraycopy(value, 0, byteArr, idx, value.length);
        idx += value.length;
    }
    
    public void putInt(int value) {
        checkOpen();

        System.arraycopy(encoder.encodeInt(value), 0, byteArr, idx, 4);
        idx += 4;
    }

    public void putShort(int value) {
        checkOpen();

        System.arraycopy(encoder.encodeShort(value), 0, byteArr, idx, 2);
        idx += 2;
    }

    public byte get() {
        checkOpen();

        return byteArr[idx++];
    }

    public int getInt() {
        checkOpen();

        var res = encoder.decodeInt(encoder.encap(Arrays.copyOfRange(byteArr, idx, idx + 4)));
        idx += 4;
        return res;
    }

    public byte get(int index) {
        return byteArr[index];
    }

    public int getInt(int index) {
        return encoder.decodeInt(encoder.encap(Arrays.copyOfRange(byteArr, index, index + 4)));
    }

    public byte[] get(int index, int length) {
        return Arrays.copyOfRange(byteArr, index, length);
    }

    public T toArray() {
        isOpen = false;
        return encoder.encap(byteArr);
    }

    private void checkOpen() {
        if (!isOpen) throw new IllegalStateException("Cannot operate under closed ByteArray.");
    }
}
