package com.mtlk.mtlkdb.struct.rawrow;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

public class RawRow {
    private String[] columns;
    private byte[][] values;
    private byte[] serialized;

    public RawRow(String[] columns, byte[][] values) {
        this.columns = columns;
        this.values = values;
    }

    public byte[] serialize() {
        if  (serialized != null) return serialized;

        int size = values[0].length;
        for (int i = 1; i < values.length; i++) {
            size += values[i].length;
        }

        var res = new byte[size];
        int k = 0;
        for (int i = 0; i < values.length; i++) {
            var value = values[i];
            System.arraycopy(value, 0, res, k, value.length);
            k += value.length;
        }

        serialized = res;
        return res;
    }

    @Nullable
    public byte[] getValue(String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(columnName)) return values[i];
        }
        return null;
    }

    public String[] getColumns() {
        return columns;
    }

    public byte[][] getValues() {
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