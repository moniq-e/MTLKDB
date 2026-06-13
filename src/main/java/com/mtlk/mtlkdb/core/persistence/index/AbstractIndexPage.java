package com.mtlk.mtlkdb.core.persistence.index;

import com.mtlk.mtlkdb.struct.util.SortedArrayList;

public abstract class AbstractIndexPage {
    protected SortedArrayList<Integer> keys;

    public AbstractIndexPage() {
        keys = new SortedArrayList<>();
    }

    public abstract byte[] serialize();

    public abstract AbstractIndexPage split(int newPageId);

    public int getFirstKey() {
        return keys.getFirst();
    }

    public int getPromotionKey() {
        return keys.get(Math.ceilDiv(keys.size(), 2));
    }

    public boolean isFull() {
        return keys.size() >= getMaxKeys();
    }

    protected abstract int getMaxKeys();
}
