package com.mtlk.mtlkdb.struct.util;

public class ScanRange {
    private Integer low;
    private Integer high;
    private Integer[] specificIds;

    public ScanRange(Integer low, Integer high) {
        this.low = low;
        this.high = high;
        this.specificIds = null;
    }

    public ScanRange(Integer... specificIds) {
        this.specificIds = specificIds;
    }

    public boolean isSpecificLookups() {
        return specificIds != null;
    }

    public Integer[] getSpecificIds() {
        return specificIds;
    }

    public Integer getLow() {
        return low;
    }

    public Integer getHigh() {
        return high;
    }
}