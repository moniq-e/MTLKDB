package com.mtlk.mtlkdb.core.persistence;

import java.util.Arrays;

import com.mtlk.mtlkdb.struct.util.Consts;
import com.mtlk.mtlkdb.struct.util.Encoder;

public class Page {
    private final byte[] data;
    private short headerSize;
    private byte[] header;

    public Page(byte[] data) {
        this.data = data;
        this.headerSize = data[0];
        this.header = new byte[headerSize];

        System.arraycopy(data, Consts.HEADER_SIZE_BYTES, header, 0, headerSize);
    }

    public byte[] getRecord(int slotId) {
        int offsetIndex = slotId * Consts.HEADER_SLOT_SIZE_BYTES;

        int recordSizeIdx = Encoder.decodeShort(Arrays.copyOfRange(data, offsetIndex, offsetIndex + Consts.HEADER_SLOT_SIZE_BYTES));
        int recordStart = recordSizeIdx + Consts.RECORD_SIZE_BYTES;

        int size = Encoder.decodeInt(Arrays.copyOfRange(data, recordSizeIdx, recordSizeIdx + Consts.RECORD_SIZE_BYTES));

        byte[] recordBytes = new byte[size];
        System.arraycopy(data, recordStart, recordBytes, 0, size);
        return recordBytes;
    }
}