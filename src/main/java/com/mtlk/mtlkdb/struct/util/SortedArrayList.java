package com.mtlk.mtlkdb.struct.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortedArrayList<T extends Comparable<T>> extends ArrayList<T> {

    public SortedArrayList() {
        super();
    }

    public SortedArrayList(List<T> l) {
        super(l);
    }

    public int insertSorted(T value) {
        int i = Collections.binarySearch(this, value);
        int pos = i < 0 ? (-i - 1) : i;
        add(pos, value);
        return pos;
    }
}