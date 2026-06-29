package com.mtlk.mtlkdb.struct.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ScanRange {
    private Integer low = 0;
    private Integer high = Integer.MAX_VALUE;
    private Set<Integer> specificIds;

    public ScanRange(int low, int high) {
        this.low = low;
        this.high = high == -1 ? Integer.MAX_VALUE : high;
        specificIds = Collections.emptySet();
    }

    public ScanRange(Integer... idsArray) {
        specificIds = new LinkedHashSet<>();
        if (idsArray != null) specificIds.addAll(ArrayAsCollection.of(idsArray));
    }

    public ScanRange(Set<Integer> aIds, Set<Integer> bIds) {
        specificIds = new LinkedHashSet<>();
        if (aIds != null) specificIds.addAll(aIds);
        if (bIds != null) specificIds.addAll(bIds);
    }

    public static ScanRange merge(ScanRange a, ScanRange b) {
        if (a == null) return b;
        if (b == null) return a;

        var res = new ScanRange(a.getSpecificIds(), b.getSpecificIds());

        if (a.hasRange() || b.hasRange()) {
            res.high = Math.max(a.high, b.high);
            res.low = Math.min(a.low, b.low);
        }
        return res;
    }

    public static ScanRange mergeAnd(ScanRange a, ScanRange b) {
        if (a == null) return b;
        if (b == null) return a;

        var res = new ScanRange(Collections.emptySet(), Collections.emptySet());
        res.high = Math.min(a.high, b.high);
        res.low = Math.max(a.low, b.low);
        return res;
    }

    public boolean hasRange() {
        return low != 0 || high != Integer.MAX_VALUE;
    }

    public Set<Integer> getSpecificIds() {
        return specificIds;
    }

    public Integer getLow() {
        return low;
    }

    public Integer getHigh() {
        return high;
    }
}