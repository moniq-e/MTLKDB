package struct.value;

import org.jetbrains.annotations.NotNull;

import struct.Row;

public class ColumnValue extends LiteralValue {
    private String columnName;
    
    public ColumnValue(String value) {
        super((byte[]) null);
        columnName = value;
    }

    @Override
    @NotNull
    public LiteralValue evaluate(Row row) {
        return new LiteralValue(row.getValue(columnName));
    }

    @Override
    public int compareTo(LiteralValue o) {
        throw new UnsupportedOperationException("Unable to call 'compareTo' over ColumnValue");
    }
}
