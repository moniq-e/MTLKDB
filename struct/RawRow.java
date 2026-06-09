package struct;

import org.jetbrains.annotations.Nullable;

public class RawRow {
    String[] columns;
    byte[][] values;

    public RawRow(String[] columns, byte[][] values) {
        this.columns = columns;
        this.values = values;
    }

    @Nullable
    public byte[] getValue(String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(columnName)) return values[i];
        }
        return null;
    }
}