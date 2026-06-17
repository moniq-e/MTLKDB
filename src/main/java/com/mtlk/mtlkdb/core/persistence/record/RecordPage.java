package com.mtlk.mtlkdb.core.persistence.record;

import static com.mtlk.mtlkdb.core.persistence.index.IndexManager.PAGE_SIZE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.mtlk.mtlkdb.core.persistence.SerializablePage;
import com.mtlk.mtlkdb.struct.IndexPageType;
import com.mtlk.mtlkdb.struct.util.ByteArray;
import com.mtlk.mtlkdb.struct.util.Encoder;

public class RecordPage implements SerializablePage{
    public static final int VARCHAR_SIZE_BYTES = 2;
    public static final int RECORD_SIZE_BYTES = 4;
    public static final int HEADER_SIZE_BYTES = 2;
    public static final int HEADER_SLOT_SIZE_BYTES = 2;

    private ArrayList<Short> header;
    private ArrayList<byte[]> records;

    private RecordPage() {
        this.header = new ArrayList<>();
        this.records = new ArrayList<>();
    }

    public static RecordPage deserialize(byte[] data) {
        var page = new RecordPage();

        var headerSlotCount = Encoder.decodeShort(Arrays.copyOfRange(data, 0, HEADER_SIZE_BYTES));

        for (int i = 0; i < headerSlotCount; i += HEADER_SLOT_SIZE_BYTES) {
            var slotPos = HEADER_SIZE_BYTES + i;

            var realPos = Encoder.decodeShort(Arrays.copyOfRange(data, slotPos, slotPos + HEADER_SLOT_SIZE_BYTES));
            page.header.add(realPos);

            int recordSize = Encoder.decodeInt(Arrays.copyOfRange(data, realPos, realPos + RECORD_SIZE_BYTES));
            int recordStart = realPos + RECORD_SIZE_BYTES;

            page.records.add(Arrays.copyOfRange(data, recordStart, recordSize));
        }
        return page;
    }

    @Override
    public byte[] serialize() {
        var buffer = ByteArray.allocate(PAGE_SIZE);

        buffer.putShort(header.size());

        for (int i = 0; i < header.size(); i++) {
            var realPos = header.get(i);
            buffer.putShort(realPos);
            buffer.put(records.get(i), realPos);
        }

        return buffer.toArray();
    }

    public byte[] getRecord(int slotId) {
        return records.get(slotId);
    }

    public int insertRecord(byte[] record) {
        var nextFreePos = records.get(header.getLast()).length;

        if (nextFreePos < PAGE_SIZE - record.length) return -1;

        header.add((short) nextFreePos);
        records.add(record);

        return nextFreePos;
    }

    public void removeRecord(int slotId) {
        header.remove(slotId);
        var removedSize = records.remove(slotId).length;

        for (int i = slotId; i < header.size(); i++) {
            header.set(i, (short) (header.get(i) - removedSize));
        }
    }

    public void removeRecord(int fromSlotId, int toSlotId) {
        header.subList(fromSlotId, toSlotId + 1).clear();
        var sublist = records.subList(fromSlotId, toSlotId + 1);

        int removedSize = sublist.stream().mapToInt(e -> e.length).sum();
        sublist.clear();

        if (removedSize <= 0) return;

        for (int i = fromSlotId; i < header.size(); i++) {
            header.set(i, (short) (header.get(i) - removedSize));
        }
    }
}