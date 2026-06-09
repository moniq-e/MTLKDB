package core.persistence;

// 1 byte - headerSize | headerSize bytes - header (slots de 2 bytes para cada row)
public class Page {
    private final byte[] data;
    private short headerSize;
    private byte[] header;

    public Page(byte[] data) {
        this.data = data;
        this.headerSize = data[0];
        this.header = new byte[headerSize];

        System.arraycopy(data, 1, header, 0, headerSize);
    }

    public byte[] getRecord(int slotId) {
        int offsetIndex = slotId * 2;

        int recordSizeIdx = (header[offsetIndex] << 8) | header[offsetIndex + 1];
        int recordStart = recordSizeIdx + 2;

        int size = (data[recordSizeIdx] << 8) | data[recordSizeIdx + 1];

        byte[] recordBytes = new byte[size];
        System.arraycopy(data, recordStart, recordBytes, 0, size);
        return recordBytes;
    }
}