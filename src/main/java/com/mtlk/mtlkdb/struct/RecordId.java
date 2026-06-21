package com.mtlk.mtlkdb.struct;

public record RecordId(int pageId, int slotId) implements Comparable<RecordId> {

    @Override
    public int compareTo(RecordId o) {
        var diff = pageId - o.pageId;

        if (diff == 0) return slotId - o.slotId;

        return diff;
    }
}
