package struct;

public class Row {
    String[] columns;
    Object[] values;

    public Row(String[] columns, Object[] values) {
        this.columns = columns;
        this.values = values;
    }

    public Object getValue(String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(columnName)) return values[i];
        }
        return null;
    }
}