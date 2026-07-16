package com.mtlk.mtlkdb.struct.encoder;

import java.util.Arrays;

public final class ComparableByteArray extends AbstractByteArray implements Comparable<ComparableByteArray> {

    protected ComparableByteArray(byte[] value) {
        super(value);
    }

    public static ComparableByteArray of(byte[] value) {
        var clone = value.clone();
        clone[0] ^= 0x80;
        return new ComparableByteArray(clone);
    }

    public static ComparableByteArray copyOf(ComparableByteArray original, int from, int to) {
        return copyOf(original.readOnlyValue(), from, to);
    }

    public static ComparableByteArray copyOf(byte[] original, int from, int to) {
        return new ComparableByteArray(Arrays.copyOfRange(original, from, to));
    }

    @Override
    public int compareTo(ComparableByteArray o) {
        return Arrays.compare(value(), o.value());
    }

    @Override
    public byte[] decode() {
        var res = value();
        res[0] ^= 0x80;
        return res;
    }

    public PersistByteArray toPersist() {
        return new PersistByteArray(decode());
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}