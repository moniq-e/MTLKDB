package com.mtlk.mtlkdb.struct.encoder;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

public abstract sealed class AbstractByteArray implements Cloneable permits ComparableByteArray, PersistByteArray {
    @NotNull
    private final byte[] value;

    protected AbstractByteArray(@NotNull byte[] value) {
        this.value = value;
    }

    protected byte[] readOnlyValue() {
        return value;
    }

    public byte[] value() {
        return value.clone();
    }

    public byte[] decode() {
        return value();
    }

    public int length() {
        return value.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        return Arrays.equals(value, ((AbstractByteArray) obj).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}