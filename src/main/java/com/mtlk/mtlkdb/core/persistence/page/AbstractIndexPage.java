package com.mtlk.mtlkdb.core.persistence.page;

import com.mtlk.mtlkdb.struct.util.SortedArrayList;

public abstract class AbstractIndexPage {
    public SortedArrayList<Integer> keys;

    public AbstractIndexPage() {
        keys = new SortedArrayList<>();
    }

    public abstract byte[] serialize();
    
    public abstract boolean isFull();
}
