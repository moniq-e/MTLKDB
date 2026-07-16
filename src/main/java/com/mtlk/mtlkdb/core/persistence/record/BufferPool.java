package com.mtlk.mtlkdb.core.persistence.record;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

import com.mtlk.mtlkdb.core.persistence.DiskManager;
import com.mtlk.mtlkdb.struct.FrameUsage;
import com.mtlk.mtlkdb.struct.RecordId;
import com.mtlk.mtlkdb.struct.encoder.PersistByteArray;

public class BufferPool implements Closeable {
    private static final int PAGE_SIZE = DiskManager.PAGE_SIZE;
    private static final int NUM_PAGES = 16;

    private byte[] memory; 
    private int[] frameToPageId; 

    private short dirtyFlags;
    private short occupiedFlags;

    private FrameUsage frameMan;
    private DiskManager diskManager;

    public BufferPool(Path path) throws IOException {
        this.diskManager = new DiskManager(path);
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

            readFromDiskToMemory(pageId, freeFrame);
            frameMan.update(freeFrame);
            return extractPage(freeFrame);
        }

        var lruFrame = frameMan.getLRU();
        var lruPageId = frameToPageId[lruFrame];

        if (checkDirty(lruFrame)) {
            diskManager.writePage(lruPageId, readFromMemory(lruFrame));

            unmarkDirty(lruFrame);
            unmarkOccupied(lruFrame);
        }

        return getPage(pageId);
    }

    public void writePage(RecordPage page) throws IOException {
        int frameId;
        for (frameId = 0; frameId < frameToPageId.length; frameId++) {
            if (frameToPageId[frameId] == page.getId()) break;
        }

        if (frameId < frameToPageId.length) {
            writeToMemory(frameId, page.serialize());
            markDirty(frameId);
        } else {
            diskManager.writePage(page.getId(), page);
        }
    }

    public RecordId insertRecord(PersistByteArray record) throws IOException {
        var totalPages = getTotalPages();

        for (int i = 0; i < totalPages; i++) {
            var page = RecordPage.deserialize(i, diskManager.readPage(i));

            int slotId = page.insertRecord(record);
            if (slotId != -1) {
                diskManager.writePage(i, page.serialize());
                return new RecordId(i, slotId);
            }
        }

        var newPage = new RecordPage(totalPages);
        int slotId = newPage.insertRecord(record);

        diskManager.writePage(newPage.getId(), newPage.serialize());
        return new RecordId(newPage.getId(), slotId);
    }

    public int getTotalPages() {
        var fileSize = diskManager.getFileSize();
        if (fileSize <= 0) return 0;
        return (int) Math.ceil(fileSize / (double) PAGE_SIZE);
    }

    private RecordPage extractPage(int frameId) {
        return RecordPage.deserialize(frameId, readFromMemory(frameId));
    }

    private PersistByteArray readFromMemory(int frameId) {
        var index = frameId * PAGE_SIZE;
        return PersistByteArray.copyOf(memory, index, index + PAGE_SIZE);
    }

    private void writeToMemory(int frameId, PersistByteArray data) {
        System.arraycopy(data, 0, memory, frameId * PAGE_SIZE, PAGE_SIZE);
    }

    private void readFromDiskToMemory(int pageId, int frameId) throws IOException {
        var data = diskManager.readPage(pageId);
        writeToMemory(frameId, data);
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

    @Override
    public void close() throws IOException {
        for (int i = 0; i < NUM_PAGES; i++) {
            if (!checkOccupied(i)) continue;
            diskManager.writePage(frameToPageId[i], readFromMemory(i));
        }
        diskManager.close();
    }
}