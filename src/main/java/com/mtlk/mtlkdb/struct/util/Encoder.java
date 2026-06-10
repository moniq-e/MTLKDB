package com.mtlk.mtlkdb.struct.util;

public class Encoder {

    private Encoder() {}

    public static byte[] encodeString(String value) {
        var size = value.length();
        var bytes = new byte[Consts.VARCHAR_SIZE_BYTES + size];

        System.arraycopy(encode(size, Consts.VARCHAR_SIZE_BYTES), 0, bytes, 0, Consts.VARCHAR_SIZE_BYTES);
        System.arraycopy(value.getBytes(), 0, bytes, Consts.VARCHAR_SIZE_BYTES, size);
        return bytes;
    }

    public static byte[] encode(int value, int byteCount) {
        var bytes = new byte[byteCount];
        value ^= 1 << (8 * byteCount - 1);

        for (int i = byteCount - 1; i >= 0; i--) {
            bytes[i] = (byte) value;
            value >>>= 8;
        }
        return bytes;
    }

    public static byte[] encodeShort(int value) {
        var bytes = new byte[2];
        value ^= 0x8000; 

        bytes[0] = (byte) (value >>> 8);
        bytes[1] = (byte) value;
        return bytes;
    }

    public static short decodeShort(byte[] bytes) {
        short value = 0;

        value |= (bytes[0] & 0xFF) << 8;
        value |= (bytes[1] & 0xFF);

        value ^= 0x8000;
        return value;
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

    public static int decodeInt(byte[] bytes) {
        int value = 0;

        for (int i = 0; i < 4; i++) {
            value |= (bytes[i] & 0xFF) << (24 - 8 * i);
        }

        value ^= 0x80000000;
        return value;
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

    public static long decodeLong(byte[] bytes) {
        int value = 0;

        for (int i = 0; i < 8; i++) {
            value |= (bytes[i] & 0xFF) << (56 - 8 * i);
        }

        value ^= 0x8000000000000000L;
        return value;
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
