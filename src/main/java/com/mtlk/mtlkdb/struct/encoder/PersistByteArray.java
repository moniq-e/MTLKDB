package com.mtlk.mtlkdb.struct.encoder;

import java.util.Arrays;

public final class PersistByteArray extends AbstractByteArray {

    protected PersistByteArray(byte[] value) {
        super(value);
    }

    public static PersistByteArray of(byte[] value) {
        return new PersistByteArray(value.clone());
    }

    public static PersistByteArray copyOf(PersistByteArray original, int from, int to) {
        return copyOf(original.readOnlyValue(), from, to);
    }

    public static PersistByteArray copyOf(byte[] original, int from, int to) {
        return new PersistByteArray(Arrays.copyOfRange(original, from, to));
    }

    public ComparableByteArray toComparable() {
        return new ComparableByteArray(decode());
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