package com.mtlk.mtlkdb.struct;

public enum IndexPageType {
    INTERNAL,
    LEAF;

    public byte get() {
        return (byte) ordinal();
    }
}
