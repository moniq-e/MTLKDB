package com.mtlk.mtlkdb.struct.rawrow;

import static com.mtlk.mtlkdb.struct.encoder.Encoder.PERSIST;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.struct.encoder.ComparableByteArray;
import com.mtlk.mtlkdb.struct.encoder.PersistByteArray;
import com.mtlk.mtlkdb.struct.util.ByteBufferMan;

public class RawRow {
    private String[] columns;
    private ComparableByteArray[] values;
    private PersistByteArray serialized;

    public RawRow(String[] columns, ComparableByteArray[] values) {
        this.columns = columns;
        this.values = values;
    }

    public PersistByteArray serialize() {
        if (serialized != null) return serialized;

        int size = values[0].length();
        for (int i = 1; i < values.length; i++) {
            size += values[i].length();
        }

        var res = ByteBufferMan.allocate(size, PERSIST);
        for (int i = 0; i < values.length; i++) {
            res.put(values[i].decode());
        }

        serialized = res.toArray();
        return serialized;
    }

    @Nullable
    public ComparableByteArray getValue(String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(columnName)) return values[i];
        }
        return null;
    }

    public String[] getColumns() {
        return columns;
    }

    public ComparableByteArray[] getValues() {
        return values;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        var other = (RawRow) obj;
        if (!Arrays.equals(columns, other.columns)) return false;
        if (!Arrays.deepEquals(values, other.values)) return false;
        return true;
    }
}