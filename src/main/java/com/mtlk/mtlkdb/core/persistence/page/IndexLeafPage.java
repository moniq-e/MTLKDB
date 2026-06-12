package com.mtlk.mtlkdb.core.persistence.page;

import static com.mtlk.mtlkdb.core.persistence.IndexManager.PAGE_SIZE;

import java.util.ArrayList;
import java.util.Collections;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.struct.RecordId;
import com.mtlk.mtlkdb.struct.util.ByteArray;
import com.mtlk.mtlkdb.struct.util.SortedArrayList;

public class IndexLeafPage {
    private static final int HEADER_SIZE = 1 + 4 + 4; 
    private static final int ENTRY_SIZE = 4 + 4 + 4; 

    public static final int MAX_KEYS = (PAGE_SIZE - HEADER_SIZE) / ENTRY_SIZE;

    private int nextPageId;
    private SortedArrayList<Integer> keys;
    private ArrayList<RecordId> rids;

    private IndexLeafPage() {
        this.nextPageId = -1;
        this.keys = new SortedArrayList<>();
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

        var type = buffer.get(); // Ignora ou valida se é folha mesmo
        if (type != 1) return null;

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
}
