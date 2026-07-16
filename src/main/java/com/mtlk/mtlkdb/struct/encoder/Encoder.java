package com.mtlk.mtlkdb.struct.encoder;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import com.mtlk.mtlkdb.core.persistence.record.RecordPage;

public final class Encoder<T extends AbstractByteArray> {
    public static final Encoder<ComparableByteArray> COMPARABLE = new Encoder<>(ComparableByteArray::of);
    public static final Encoder<PersistByteArray> PERSIST = new Encoder<>(PersistByteArray::of);

    private Function<byte[], T> encapsulator;

    private Encoder(Function<byte[], T> function) {
        encapsulator = function;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractByteArray> Encoder<T> getEncoder(T sample) {
        if (sample.getClass() == ComparableByteArray.class) {
            return (Encoder<T>) COMPARABLE;
        } else if (sample.getClass() == PersistByteArray.class) {
            return (Encoder<T>) PERSIST;
        }
        throw new IllegalArgumentException("No encoder registered for: " + sample.getClass());
    }

    @NotNull
    public T encap(byte[] value) {
        return encapsulator.apply(value);
    }

    public T encodeString(String value) {
        var size = value.length();
        var bytes = new byte[RecordPage.VARCHAR_SIZE_BYTES + size];

        System.arraycopy(encode(size, RecordPage.VARCHAR_SIZE_BYTES), 0, bytes, 0, RecordPage.VARCHAR_SIZE_BYTES);
        System.arraycopy(value.getBytes(), 0, bytes, RecordPage.VARCHAR_SIZE_BYTES, size);
        return encap(bytes);
    }

    public T encode(int value, int byteCount) {
        var bytes = new byte[byteCount];

        for (int i = byteCount - 1; i >= 0; i--) {
            bytes[i] = (byte) value;
            value >>>= 8;
        }
        return encap(bytes);
    }

    public T encodeShort(int value) {
        var bytes = new byte[2];

        bytes[0] = (byte) (value >>> 8);
        bytes[1] = (byte) value;
        return encap(bytes);
    }

    public short decodeShort(T tBytes) {
        var bytes = tBytes.decode();
        short value = 0;

        value |= (bytes[0] & 0xFF) << 8;
        value |= (bytes[1] & 0xFF);

        return value;
    }

    public T encodeInt(int value) {
        var bytes = new byte[4];
        
        bytes[0] = (byte) (value >>> 24);
        bytes[1] = (byte) (value >>> 16);
        bytes[2] = (byte) (value >>> 8);
        bytes[3] = (byte) value;
        return encap(bytes);
    }

    public int decodeInt(T tBytes) {
        var bytes = tBytes.decode();
        int value = 0;

        for (int i = 0; i < 4; i++) {
            value |= (bytes[i] & 0xFF) << (24 - 8 * i);
        }

        return value;
    }

    public T encodeLong(long value) {
        var bytes = new byte[8];
        
        bytes[0] = (byte) (value >>> 56);
        bytes[1] = (byte) (value >>> 48);
        bytes[2] = (byte) (value >>> 40);
        bytes[3] = (byte) (value >>> 32);
        bytes[4] = (byte) (value >>> 24);
        bytes[5] = (byte) (value >>> 16);
        bytes[6] = (byte) (value >>> 8);
        bytes[7] = (byte) value;
        return encap(bytes);
    }

    public long decodeLong(T tBytes) {
        var bytes = tBytes.decode();
        long value = 0L;

        for (int i = 0; i < 8; i++) {
            value |= (bytes[i] & 0xFF) << (56 - 8 * i);
        }

        return value;
    }

    public T encodeFloat(float value) {
        int bits = Float.floatToIntBits(value);

        if (bits < 0) {
            bits ^= 0x7FFFFFFF;
        }

        var bytes = new byte[4];
        bytes[0] = (byte) (bits >>> 24);
        bytes[1] = (byte) (bits >>> 16);
        bytes[2] = (byte) (bits >>> 8);
        bytes[3] = (byte) bits;
        return encap(bytes);
    }

    public float decodeFloat(T tBytes) {
        var bytes = tBytes.decode();
        int bits = 0;

        for (int i = 0; i < 4; i++) {
            bits |= (bytes[i] & 0xFF) << (24 - 8 * i);
        }

        if (bits < 0) {
            bits ^= 0x7FFFFFFF;
        }

        return Float.intBitsToFloat(bits);
    }

    public T encodeDouble(double value) {
        long bits = Double.doubleToLongBits(value);

        if (bits < 0) {
            bits ^= 0x7FFFFFFFFFFFFFFFL;
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
        return encap(bytes);
    }

    public double decodeDouble(T tBytes) {
        var bytes = tBytes.decode();
        long bits = 0;

        for (int i = 0; i < 8; i++) {
            bits |= (bytes[i] & 0xFF) << (56 - 8 * i);
        }

        if (bits < 0) {
            bits ^= 0x7FFFFFFFFFFFFFFFL;
        }

        return Double.longBitsToDouble(bits);
    }
}
