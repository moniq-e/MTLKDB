package com.mtlk.mtlkdb.struct.encoder;

import static com.mtlk.mtlkdb.struct.encoder.Encoder.COMPARABLE;

import org.jetbrains.annotations.Nullable;

public class RawRowEncoder {

    private RawRowEncoder() {}

    public static ComparableByteArray[] encodeValues(String[] values) {
        var res = new ComparableByteArray[values.length];
        for (int i = 0; i < values.length; i++) {
            res[i] = encodeString(values[i]);
        }
        return res;
    }

    @Nullable
    public static ComparableByteArray encodeString(String value) {
        if (value == null) return null;
        try {
            if (!value.contains(".")) {
                try {
                    return COMPARABLE.encodeInt(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    return COMPARABLE.encodeLong(Long.parseLong(value));
                }
            } else {
                try {
                    return COMPARABLE.encodeFloat(Float.parseFloat(value));
                } catch (NumberFormatException e) {
                    return COMPARABLE.encodeDouble(Double.parseDouble(value));
                }
            }
        } catch (Exception e) {
            return COMPARABLE.encodeString(value);
        }
    }
}