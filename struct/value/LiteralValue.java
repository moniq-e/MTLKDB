package struct.value;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import struct.RawRow;
import struct.util.RawEncoder;

public class LiteralValue implements Comparable<LiteralValue> {
    private byte[] buffer;

    public LiteralValue(String value) {
        buffer = RawEncoder.encodeString(value);
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
