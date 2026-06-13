package com.mtlk.mtlkdb.core.persistence.index;

import static com.mtlk.mtlkdb.core.persistence.index.IndexManager.PAGE_SIZE;

import java.util.ArrayList;
import java.util.Collections;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.struct.IndexPageType;
import com.mtlk.mtlkdb.struct.util.ByteArray;

public class IndexInternalPage extends AbstractIndexPage {
    private static final int HEADER_SIZE = 1 + 4;
    private static final int ENTRY_SIZE = 4 + 4;

    public static final int MAX_KEYS = (PAGE_SIZE - HEADER_SIZE - 4) / ENTRY_SIZE;

    private ArrayList<Integer> childPageIds; 

    public IndexInternalPage(int firstChildId) {
        super();
        childPageIds.add(firstChildId);
    }

    public byte[] serialize() {
        var buffer = ByteArray.allocate(PAGE_SIZE);
        
        buffer.put(IndexPageType.INTERNAL.get());
        buffer.putInt(keys.size());
        
        buffer.putInt(childPageIds.get(0));
        
        for (int i = 0; i < keys.size(); i++) {
            buffer.putInt(keys.get(i));
            buffer.putInt(childPageIds.get(i + 1));
        }
        
        return buffer.toArray();
    }

    @Nullable
    public static IndexInternalPage deserialize(byte[] pageData) {
        var buffer = new ByteArray(pageData);
        
        var type = buffer.get();
        if (type != IndexPageType.INTERNAL.get()) return null;
        
        int keyCount = buffer.getInt();
        var page = new IndexInternalPage(buffer.getInt());
        
        for (int i = 0; i < keyCount; i++) {
            int key = buffer.getInt();
            int childPageId = buffer.getInt();
            
            page.keys.add(key);
            page.childPageIds.add(childPageId);
        }
        return page;
    }

    public int getChildPageId(int key) {
        var index = Collections.binarySearch(keys, key);

        if (index >= 0) {
            return childPageIds.get(index + 1);
        }

        var pos = -index - 1;
        return childPageIds.get(pos);
    }

    public void insertChildPageId(int key, int childPageId) {
        var pos = keys.insertSorted(key);
        childPageIds.add(pos + 1, childPageId);
    }

    @Override
    public AbstractIndexPage split(int newPageId) {
        var newPageBuffer = ByteArray.allocate(PAGE_SIZE);

        var mid = Math.ceilDiv(keys.size(), 2);

        newPageBuffer.put(IndexPageType.INTERNAL.get());
        newPageBuffer.putInt(keys.size() - (mid + 1));
        newPageBuffer.putInt(childPageIds.get(mid + 1));

        for (int i = mid + 1; i < keys.size(); i++) {
            newPageBuffer.putInt(keys.get(i));
            newPageBuffer.putInt(childPageIds.get(i + 1));
        }

        keys.subList(mid, keys.size()).clear();
        childPageIds.subList(mid + 1, childPageIds.size()).clear();

        return deserialize(newPageBuffer.toArray());
    }

    @Override
    protected int getMaxKeys() {
        return MAX_KEYS;
    }
}
