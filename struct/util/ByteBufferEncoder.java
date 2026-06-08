package struct.util;

import org.jetbrains.annotations.Nullable;

public class ByteBufferEncoder {

    private ByteBufferEncoder() {}

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
                    return encodeInt(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    return encodeLong(Long.parseLong(value));
                }
            } else {
                try {
                    return encodeFloat(Float.parseFloat(value));
                } catch (NumberFormatException e) {
                    return encodeDouble(Double.parseDouble(value));
                }
            }
        } catch (Exception e) {
            return encodeStringInternal(value);
        }
    }

    private static byte[] encodeStringInternal(String value) {
        var size = value.length();
        var bytes = new byte[4 + size];

        size ^= 0x80000000; 
        bytes[0] = (byte) (size >>> 24);
        bytes[1] = (byte) (size >>> 16);
        bytes[2] = (byte) (size >>> 8);
        bytes[3] = (byte) size;

        System.arraycopy(value.getBytes(), 0, bytes, 4, size);
        return bytes;
    }

    public static byte[] encodeInt(int value) {
        var bytes = new byte[4];
        value ^= 0x80000000; 
        
        bytes[0] = (byte) (value >>> 24);
        bytes[1] = (byte) (value >>> 16);
        bytes[2] = (byte) (value >>> 8);
        bytes[3] = (byte) value;
        return bytes;
    }

    public static byte[] encodeLong(long value) {
        var bytes = new byte[8];
        value ^= 0x8000000000000000L;
        
        bytes[0] = (byte) (value >>> 56);
        bytes[1] = (byte) (value >>> 48);
        bytes[2] = (byte) (value >>> 40);
        bytes[3] = (byte) (value >>> 32);
        bytes[4] = (byte) (value >>> 24);
        bytes[5] = (byte) (value >>> 16);
        bytes[6] = (byte) (value >>> 8);
        bytes[7] = (byte) value;
        return bytes;
    }

    public static byte[] encodeFloat(float value) {
        int bits = Float.floatToIntBits(value);

        if (bits >= 0) {
            bits ^= 0x80000000;
        } else {
            bits = ~bits;
        }

        var bytes = new byte[4];
        bytes[0] = (byte) (bits >>> 24);
        bytes[1] = (byte) (bits >>> 16);
        bytes[2] = (byte) (bits >>> 8);
        bytes[3] = (byte) bits;
        return bytes;
    }

    public static byte[] encodeDouble(double value) {
        long bits = Double.doubleToLongBits(value);

        if (bits >= 0) {
            bits ^= 0x8000000000000000L;
        } else {
            bits = ~bits;
        }

        var bytes = new byte[8];
        bytes[0] = (byte) (bits >>> 56);
        bytes[1] = (byte) (bits >>> 48);
        bytes[2] = (byte) (bits >>> 40);
        bytes[3] = (byte) (bits >>> 32);
        bytes[4] = (byte) (bits >>> 24);
        bytes[5] = (byte) (bits >>> 16);
        bytes[6] = (byte) (bits >>> 8);
        bytes[7] = (byte) bits;
        return bytes;
    }
}