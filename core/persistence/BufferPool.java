package core.persistence;

import java.io.IOException;

import struct.FrameUsage;

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

    public Page getPage(int pageId) throws IOException {
        for (int i = 0; i < NUM_PAGES; i++) {
            if (((occupiedFlags >> i) & 1) == 1 && frameToPageId[i] == pageId) {
                frameMan.update(i);
                return extractPage(i);
            }
        }

        int freeFrame = -1;
        for (int i = 0; i < NUM_PAGES; i++) {
            if (((occupiedFlags >> i) & 1) == 0) {
                freeFrame = i;
                break;
            }
        }

        if (freeFrame != -1) {
            occupiedFlags |= (1 << freeFrame);
            frameToPageId[freeFrame] = pageId;

            diskPageToMemory(pageId);
            frameMan.update(freeFrame);
            return extractPage(pageId);
        }
        
        var lruFrame = frameMan.getLRU();
        var lruPageId = frameToPageId[lruFrame];

        if (((dirtyFlags >> lruFrame) & 1) == 1) {
            diskManager.writePage(lruPageId, extractRawPage(lruPageId));

            dirtyFlags ^= (1 << lruFrame);
            occupiedFlags ^= (1 << lruFrame);
        }

        return getPage(pageId);
    }
    
    public void markDirty(int frameId) {
        dirtyFlags |= (1 << frameId);
    }

    private Page extractPage(int pageId) {
        return new Page(extractRawPage(pageId));
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
}