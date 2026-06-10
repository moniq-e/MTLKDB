package core.persistence;

import java.io.IOException;

import struct.ColumnDefinition;
import struct.RawRow;
import struct.util.Consts;

public class Table {
    private String tableName;
    private ColumnDefinition[] columns;
    private String[] columnsNames;
    private BufferPool rows;

    public Table(String tableName, ColumnDefinition[] columns) throws IOException {
        this.tableName = tableName;
        this.columns = columns;
        updateColumnsNames();
        rows = new BufferPool(tableName + ".dat");
    }

    public RawRow deserializeRow(byte[] record) {
        var rowData = new byte[columns.length][];

        int k = 0;
        for (int i = 0; i < columns.length; i++) {
            var colType = columns[i].columnType();

            if (colType.isVarchar()) {
                var varsize = (record[k] << 8) | record[k + 1];

                System.arraycopy(record, k + Consts.VARCHAR_SIZE_BYTES, rowData[i], 0, varsize);
                k += Consts.VARCHAR_SIZE_BYTES + varsize;
            } else {
                System.arraycopy(record, k, rowData[i], 0, colType.getSize());
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
