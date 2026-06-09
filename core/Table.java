package core;

import java.io.IOException;

import core.persistence.BufferPool;
import struct.ColumnDefinition;
import struct.RawRow;

public class Table {
    private String tableName;
    private ColumnDefinition[] columns;
    private String[] columnsNames;
    private BufferPool rows;

    public Table(String tableName, ColumnDefinition[] columns) throws IOException {
        this.tableName = tableName;
        this.columns = columns;
        updateColumnsNames();
        rows = new BufferPool(tableName);
    }

    public RawRow deserializeRow(byte[] data) {
        var rowData = new byte[columns.length][];

        int k = 0;
        for (int i = 0; i < columns.length; i++) {
            var colType = columns[i].columnType();

            if (colType.isVarchar()) {
                var varsize = (data[k] << 8) | data[k + 1];

                System.arraycopy(data, k + 2, rowData[i], 0, varsize);
                k += 2 + varsize;
            } else {
                System.arraycopy(data, k, rowData[i], 0, colType.getSize());
                k += colType.getSize();
            }
        }
        return new RawRow(columnsNames, rowData);
    }

    public void updateColumnsNames() {
        columnsNames = new String[columns.length];

        for (int i = 0; i < columns.length; i++) {
            columnsNames[i] = columns[i].name();
        }
    }
}
