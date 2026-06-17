package com.mtlk.mtlkdb.core.persistence.index;

import java.util.Collections;

import com.mtlk.mtlkdb.core.persistence.SerializablePage;
import com.mtlk.mtlkdb.struct.util.SortedArrayList;

public abstract class AbstractIndexPage implements SerializablePage {
    protected SortedArrayList<Integer> keys;

    public AbstractIndexPage() {
        keys = new SortedArrayList<>();
    }

    public abstract AbstractIndexPage split(int thisPageId, int newPageId);

    public int getFirstKey() {
        return keys.getFirst();
    }

    public int getPromotionKey() {
        return keys.get(Math.ceilDiv(keys.size(), 2));
    }

    protected int getKeyIndex(int key) {
        return Collections.binarySearch(keys, key);
    }

    public boolean isFull() {
        return keys.size() >= getMaxKeys();
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    protected abstract int getMaxKeys();
}
