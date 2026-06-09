package core.persistence;

import java.io.IOException;

public class BufferPool {
    private static final int PAGE_SIZE = DiskManager.PAGE_SIZE;
    private static final int NUM_PAGES = 16;

    private byte[] memory; 
    private int[] pageTable; 

    private short dirtyFlags;
    private short occupiedFlags;

    private DiskManager diskManager;

    public BufferPool(DiskManager diskManager) {
        this.diskManager = diskManager;
        this.memory = new byte[PAGE_SIZE * NUM_PAGES];
        this.pageTable = new int[NUM_PAGES];
        this.dirtyFlags = 0;
        this.occupiedFlags = 0;
        
        for (int i = 0; i < NUM_PAGES; i++) pageTable[i] = -1;
    }

    public Page getPage(int pageId) throws IOException {
        for (int i = 0; i < NUM_PAGES; i++) {
            if (((occupiedFlags >> i) & 1) == 1 && pageTable[i] == pageId) {
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
            pageTable[freeFrame] = pageId;

            diskPageToMemory(pageId);
            return extractPage(pageId);
        }
        
        throw new RuntimeException("Buffer Pool cheio! Necessário implementar algoritmo de Eviction.");
    }
    
    public void markDirty(int frameId) {
        dirtyFlags |= (1 << frameId);
    }

    private Page extractPage(int pageId) {
        var res = new byte[PAGE_SIZE];

        System.arraycopy(memory, pageId * PAGE_SIZE, res, 0, PAGE_SIZE);

        return new Page(res);
    }

    private void diskPageToMemory(int pageId) throws IOException {
        var data = diskManager.readPage(pageId);

        System.arraycopy(data, 0, memory, pageId * PAGE_SIZE, PAGE_SIZE);
    }
}