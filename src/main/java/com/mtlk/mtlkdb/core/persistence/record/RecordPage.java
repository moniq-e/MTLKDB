package com.mtlk.mtlkdb.core.persistence.record;

import java.util.Arrays;

import com.mtlk.mtlkdb.struct.util.Encoder;

public class RecordPage {
    public static final int VARCHAR_SIZE_BYTES = 2;
    public static final int RECORD_SIZE_BYTES = 4;
    public static final int HEADER_SIZE_BYTES = 1;
    public static final int HEADER_SLOT_SIZE_BYTES = 2;

    private final byte[] data;
    private short headerSize;
    private byte[] header;

    public RecordPage(byte[] data) {
        this.data = data;
        this.headerSize = data[0];
        this.header = new byte[headerSize];

        System.arraycopy(data, HEADER_SIZE_BYTES, header, 0, headerSize);
    }

    public byte[] getRecord(int slotId) {
        int offsetIndex = slotId * HEADER_SLOT_SIZE_BYTES;

        int recordSizeIdx = Encoder.decodeShort(Arrays.copyOfRange(data, offsetIndex, offsetIndex + HEADER_SLOT_SIZE_BYTES));
        int recordStart = recordSizeIdx + RECORD_SIZE_BYTES;

        int size = Encoder.decodeInt(Arrays.copyOfRange(data, recordSizeIdx, recordSizeIdx + RECORD_SIZE_BYTES));

        byte[] recordBytes = new byte[size];
        System.arraycopy(data, recordStart, recordBytes, 0, size);
        return recordBytes;
    }
}