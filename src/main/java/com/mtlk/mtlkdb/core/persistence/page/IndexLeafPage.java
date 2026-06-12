package com.mtlk.mtlkdb.core.persistence.page;

import static com.mtlk.mtlkdb.core.persistence.IndexManager.PAGE_SIZE;

import java.util.ArrayList;
import java.util.Collections;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.struct.IndexPageType;
import com.mtlk.mtlkdb.struct.RecordId;
import com.mtlk.mtlkdb.struct.util.ByteArray;

public class IndexLeafPage extends AbstractIndexPage {
    private static final int HEADER_SIZE = 1 + 4 + 4;
    private static final int ENTRY_SIZE = 4 + 4 + 4;

    public static final int MAX_KEYS = (PAGE_SIZE - HEADER_SIZE) / ENTRY_SIZE;

    private int nextPageId;
    private ArrayList<RecordId> rids;

    private IndexLeafPage() {
        this.nextPageId = -1;
        this.rids = new ArrayList<>();
    }

    public byte[] serialize() {
        var buffer = ByteArray.allocate(PAGE_SIZE);

        buffer.put((byte) 1);
        buffer.putInt(keys.size());
        buffer.putInt(nextPageId);

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

        var page = new IndexLeafPage();
        page.nextPageId = nextLeaf;

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
        var pos = Collections.binarySearch(keys, key);

        if (pos >= 0) {
            return rids.get(pos);
        }
        return null;
    }

    public void insertRecordId(int key, RecordId recordId) {
        var pos = keys.insertSorted(key);
        rids.add(pos, recordId);
    }

    public IndexLeafPage createSplittedCopy() {
        var newPageBuffer = ByteArray.allocate(PAGE_SIZE);

        newPageBuffer.put(IndexPageType.LEAF.get());
        newPageBuffer.putInt(Math.ceilDiv(keys.size(), 2));
        newPageBuffer.putInt(nextPageId);

        for (int i = keys.size() / 2; i < keys.size(); i++) {
            newPageBuffer.putInt(keys.get(i));
            newPageBuffer.putInt(rids.get(i).pageId());
            newPageBuffer.putInt(rids.get(i).slotId());
        }
        return deserialize(newPageBuffer.toArray());
    }

    public void split(int nextPageId) {
        this.nextPageId = nextPageId;
        keys.subList(keys.size() / 2, keys.size()).clear();
        rids.subList(rids.size() / 2, rids.size()).clear();
    }

    @Override
    public boolean isFull() {
        return keys.size() >= MAX_KEYS;
    }
}
