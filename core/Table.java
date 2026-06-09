package core;

import struct.ColumnDefinition;
import struct.Row;

public class Table {
    private String tableName;
    private ColumnDefinition[] columns;
    private 

    public Table(String tableName, ColumnDefinition[] columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public Row deserializeRow(byte[] data) {
        var row = new Object[columns.length];

        return null;
    }
}
