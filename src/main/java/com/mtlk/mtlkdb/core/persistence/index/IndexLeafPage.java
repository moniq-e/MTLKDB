package com.mtlk.mtlkdb.core.persistence.index;

import static com.mtlk.mtlkdb.core.persistence.index.IndexManager.PAGE_SIZE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.dto.RecordIdsDTO;
import com.mtlk.mtlkdb.struct.IndexPageType;
import com.mtlk.mtlkdb.struct.RecordId;
import com.mtlk.mtlkdb.struct.util.ByteArray;
import com.mtlk.mtlkdb.struct.util.SortedArrayList;

public class IndexLeafPage extends AbstractIndexPage {
    private static final int HEADER_SIZE = 1 + 4 + 4 + 4;
    private static final int ENTRY_SIZE = 4 + 4 + 4;

    public static final int MAX_KEYS = (PAGE_SIZE - HEADER_SIZE) / ENTRY_SIZE;

    private int nextPageId;
    private int previousPageId;
    private ArrayList<RecordId> rids;

    public IndexLeafPage(int previousPageId, int nextPageId, List<Integer> keys, List<RecordId> rids) {
        this.nextPageId = nextPageId;
        this.previousPageId = previousPageId;
        this.keys = new SortedArrayList<>(keys);
        this.rids = new ArrayList<>(rids);
    }

    private IndexLeafPage() {
        super();
        this.nextPageId = -1;
        this.previousPageId = -1;
        this.rids = new ArrayList<>();
    }

    public byte[] serialize() {
        var buffer = ByteArray.allocate(PAGE_SIZE);

        buffer.put(IndexPageType.LEAF.get());
        buffer.putInt(keys.size());
        buffer.putInt(nextPageId);
        buffer.putInt(previousPageId);

        for (int i = 0; i < keys.size(); i++) {
            buffer.putInt(keys.get(i));
            buffer.putInt(rids.get(i).pageId());
            buffer.putInt(rids.get(i).slotId());
        }

        return buffer.toArray();
    }

    @Nullable
    public static IndexLeafPage deserialize(byte[] pageData) {
        var buffer = new ByteArray(pageData);

        var type = buffer.get();
        if (type != IndexPageType.LEAF.get()) return null;

        int keyCount = buffer.getInt();
        int nextLeaf = buffer.getInt();
        int previousLeaf = buffer.getInt();

        var page = new IndexLeafPage();
        page.nextPageId = nextLeaf;
        page.previousPageId = previousLeaf;

        for (int i = 0; i < keyCount; i++) {
            int key = buffer.getInt();
            int datPageId = buffer.getInt();
            int datSlotId = buffer.getInt();

            page.keys.add(key);
            page.rids.add(new RecordId(datPageId, datSlotId));
        }
        return page;
    }

    @Nullable
    public RecordId getRecordIdByKey(int key) {
        var pos = getKeyIndex(key);

        if (pos >= 0) {
            return rids.get(pos);
        }
        return null;
    }

    public RecordIdsDTO getRecordIds(int from, int to) {
        var fromPos = getKeyIndex(from);
        var toPos = getKeyIndex(to);
        var gotonp = false;

        if (fromPos < 0) fromPos = -fromPos - 1;

        if (toPos < 0) {
            toPos = -toPos - 1;
            if (toPos == keys.size()) gotonp = true;
        } else {
            toPos++;
        }

        if (fromPos >= toPos || keys.isEmpty()) {
            return new RecordIdsDTO(null, from, true);
        }

        return new RecordIdsDTO(Collections.unmodifiableList(rids.subList(fromPos, toPos)), keys.get(toPos - 1), gotonp);
    }

    public void insert(int key, RecordId recordId) {
        var pos = keys.insertSorted(key);
        rids.add(pos, recordId);
    }

    public boolean remove(int key) {
        var pos = getKeyIndex(key);
        if (pos >= 0) {
            keys.remove(pos);
            rids.remove(pos);
            return true;
        }
        return false;
    }

    @Override
    public AbstractIndexPage split(int thisPageId, int newPageId) {
        var mid = Math.ceilDiv(keys.size(), 2);

        var secondHalfKeys = keys.subList(mid, keys.size());
        var secondHalfRids = rids.subList(mid, rids.size());
        var newPage = new IndexLeafPage(thisPageId, nextPageId, secondHalfKeys, secondHalfRids);

        nextPageId = newPageId;
        secondHalfKeys.clear();
        secondHalfRids.clear();

        return newPage;
    }

    @Override
    protected int getMaxKeys() {
        return MAX_KEYS;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof IndexLeafPage) {
            var oleaf = (IndexLeafPage) obj;
            return nextPageId == oleaf.nextPageId && 
                previousPageId == oleaf.previousPageId && 
                keys.equals(oleaf.keys) && 
                rids.equals(oleaf.rids);
        }
        return false;
    }

    public int getNextPageId() {
        return nextPageId;
    }

    public void setNextPageId(int nextPageId) {
        this.nextPageId = nextPageId;
    }

    public int getPreviousPageId() {
        return previousPageId;
    }

    public void setPreviousPageId(int previousPageId) {
        this.previousPageId = previousPageId;
    }
}
