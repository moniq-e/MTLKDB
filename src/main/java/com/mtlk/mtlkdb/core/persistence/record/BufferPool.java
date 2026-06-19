package com.mtlk.mtlkdb.core.persistence.record;

import java.io.IOException;

import com.mtlk.mtlkdb.struct.FrameUsage;

public class BufferPool {
    private static final int PAGE_SIZE = DiskManager.PAGE_SIZE;
    private static final int NUM_PAGES = 16;

    private byte[] memory; 
    private int[] frameToPageId; 

    private short dirtyFlags;
    private short occupiedFlags;

    private FrameUsage frameMan;
    private DiskManager diskManager;

    public BufferPool(String fileName) throws IOException {
        this.diskManager = new DiskManager(fileName);
        this.frameMan = new FrameUsage(NUM_PAGES);
        this.memory = new byte[PAGE_SIZE * NUM_PAGES];
        this.frameToPageId = new int[NUM_PAGES];
        this.dirtyFlags = 0;
        this.occupiedFlags = 0;
        
        for (int i = 0; i < NUM_PAGES; i++) frameToPageId[i] = -1;
    }

    public RecordPage getPage(int pageId) throws IOException {
        for (int i = 0; i < NUM_PAGES; i++) {
            if (checkOccupied(i) && frameToPageId[i] == pageId) {
                frameMan.update(i);
                return extractPage(i);
            }
        }

        int freeFrame = -1;
        for (int i = 0; i < NUM_PAGES; i++) {
            if (!checkOccupied(i)) {
                freeFrame = i;
                break;
            }
        }

        if (freeFrame != -1) {
            markOccupied(freeFrame);
            frameToPageId[freeFrame] = pageId;

            diskPageToMemory(pageId);
            frameMan.update(freeFrame);
            return extractPage(pageId);
        }

        var lruFrame = frameMan.getLRU();
        var lruPageId = frameToPageId[lruFrame];

        if (checkDirty(lruFrame)) {
            diskManager.writePage(lruPageId, extractRawPage(lruPageId));

            unmarkDirty(lruFrame);
            unmarkOccupied(lruFrame);
        }

        return getPage(pageId);
    }

    private RecordPage extractPage(int pageId) {
        return RecordPage.deserialize(extractRawPage(pageId));
    }

    private byte[] extractRawPage(int pageId) {
        var res = new byte[PAGE_SIZE];

        System.arraycopy(memory, pageId * PAGE_SIZE, res, 0, PAGE_SIZE);

        return res;
    }

    private void diskPageToMemory(int pageId) throws IOException {
        var data = diskManager.readPage(pageId);

        System.arraycopy(data, 0, memory, pageId * PAGE_SIZE, PAGE_SIZE);
    }

    private boolean checkOccupied(int frameId) {
        return ((occupiedFlags >> frameId) & 1) == 1;
    }

    private void markOccupied(int frameId) {
        occupiedFlags |= (1 << frameId);
    }

    private void unmarkOccupied(int frameId) {
        occupiedFlags &= ~(1 << frameId);
    }

    private boolean checkDirty(int frameId) {
        return ((dirtyFlags >> frameId) & 1) == 1;
    }

    private void markDirty(int frameId) {
        dirtyFlags |= (1 << frameId);
    }

    private void unmarkDirty(int frameId) {
        dirtyFlags &= ~(1 << frameId);
    }
}