package com.mtlk.mtlkdb.struct.util;

public class ScanRange {
    private Integer low;
    private Integer high;
    private int[] specificIds;

    public ScanRange(Integer low, Integer high) {
        this.low = low;
        this.high = high;
        this.specificIds = null;
    }

    public ScanRange(int... specificIds) {
        this.specificIds = specificIds;
    }

    public static ScanRange merge(ScanRange a, ScanRange b) {
        if (a == null) return b;
        if (b == null) return a;

        
    }

    public boolean isSpecificLookups() {
        return specificIds != null;
    }

    public int[] getSpecificIds() {
        return specificIds;
    }

    public Integer getLow() {
        return low;
    }

    public Integer getHigh() {
        return high;
    }
}