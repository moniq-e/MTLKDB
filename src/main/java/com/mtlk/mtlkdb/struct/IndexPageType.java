package com.mtlk.mtlkdb.struct;

public enum IndexPageType {
    LEAF,
    INTERNAL;

    public byte get() {
        return (byte) ordinal();
    }
}
