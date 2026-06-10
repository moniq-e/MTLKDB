package com.mtlk.mtlkdb.struct.util;

import org.jetbrains.annotations.Nullable;

public class RawRowEncoder {

    private RawRowEncoder() {}

    public static byte[][] encodeValues(String[] values) {
        var res = new byte[values.length][];
        for (int i = 0; i < values.length; i++) {
            res[i] = encodeString(values[i]);
        }
        return res;
    }

    @Nullable
    public static byte[] encodeString(String value) {
        if (value == null) return null;
        try {
            if (!value.contains(".")) {
                try {
                    return Encoder.encodeInt(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    return Encoder.encodeLong(Long.parseLong(value));
                }
            } else {
                try {
                    return Encoder.encodeFloat(Float.parseFloat(value));
                } catch (NumberFormatException e) {
                    return Encoder.encodeDouble(Double.parseDouble(value));
                }
            }
        } catch (Exception e) {
            return Encoder.encodeString(value);
        }
    }
}