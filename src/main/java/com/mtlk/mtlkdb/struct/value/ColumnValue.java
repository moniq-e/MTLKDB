package com.mtlk.mtlkdb.struct.value;

import org.jetbrains.annotations.NotNull;

import com.mtlk.mtlkdb.struct.rawrow.RawRow;

public class ColumnValue extends LiteralValue {
    private String columnName;
    
    public ColumnValue(String value) {
        super((byte[]) null);
        columnName = value;
    }

    @Override
    @NotNull
    public LiteralValue evaluate(RawRow row) {
        return new LiteralValue(row.getValue(columnName));
    }

    @Override
    public int compareTo(LiteralValue o) {
        throw new UnsupportedOperationException("Unable to call 'compareTo' over ColumnValue");
    }

    public String getColumnName() {
        return columnName;
    }
}
