package struct.value;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import struct.Row;
import struct.util.ByteBufferEncoder;

public class LiteralValue implements Comparable<LiteralValue> {
    private byte[] buffer;

    public LiteralValue(String value) {
        buffer = ByteBufferEncoder.encodeString(value);
    }

    protected LiteralValue(byte[] value) {
        buffer = value;
    }

    @NotNull
    public LiteralValue evaluate(Row row) {
        return this;
    }

    @Override
    public int compareTo(LiteralValue o) {
        return Arrays.compare(buffer, o.buffer);
    }
}
