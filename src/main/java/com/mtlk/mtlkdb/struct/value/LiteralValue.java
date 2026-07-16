package com.mtlk.mtlkdb.struct.value;

import static com.mtlk.mtlkdb.struct.encoder.Encoder.COMPARABLE;

import org.jetbrains.annotations.NotNull;

import com.mtlk.mtlkdb.struct.encoder.ComparableByteArray;
import com.mtlk.mtlkdb.struct.encoder.RawRowEncoder;
import com.mtlk.mtlkdb.struct.rawrow.RawRow;

public class LiteralValue implements Comparable<LiteralValue> {
    private ComparableByteArray buffer;

    public LiteralValue(String value) {
        buffer = RawRowEncoder.encodeString(value);
    }

    protected LiteralValue(ComparableByteArray value) {
        buffer = value;
    }

    @NotNull
    public LiteralValue evaluate(RawRow row) {
        return this;
    }

    @Override
    public int compareTo(LiteralValue o) {
        return buffer.compareTo(o.buffer);
    }

    public int asInt() {
        return COMPARABLE.decodeInt(buffer);
    }
}
