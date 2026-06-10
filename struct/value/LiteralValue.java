package struct.value;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import struct.RawRow;
import struct.util.RawRowEncoder;

public class LiteralValue implements Comparable<LiteralValue> {
    private byte[] buffer;

    public LiteralValue(String value) {
        buffer = RawRowEncoder.encodeString(value);
    }

    protected LiteralValue(byte[] value) {
        buffer = value;
    }

    @NotNull
    public LiteralValue evaluate(RawRow row) {
        return this;
    }

    @Override
    public int compareTo(LiteralValue o) {
        return Arrays.compare(buffer, o.buffer);
    }
}
