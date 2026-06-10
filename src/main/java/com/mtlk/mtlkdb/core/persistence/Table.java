package com.mtlk.mtlkdb.core.persistence;

import java.io.File;
import java.io.IOException;

import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.util.Consts;

public class Table {
    private String tableName;
    private File tableFolder;
    private ColumnDefinition[] columns;
    private String[] columnsNames;
    private BufferPool rows;

    public Table(String tableName, File tableFolder) throws IOException {
        this.tableName = tableName;
        this.tableFolder = tableFolder;
        readColumnDefinition();
        updateColumnsNames();
        rows = new BufferPool(tableName + ".dat");
    }

    private void readColumnDefinition() throws IOException {
        var files = tableFolder.listFiles((d, f) -> f.endsWith(".inf"));
        if (files.length < 1) throw new IOException(tableName + " .inf file can't be found.");

        
    }

    public static String extractTableName(File folder) {
        return folder.getName().split("table_")[1];
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

    private void updateColumnsNames() {
        columnsNames = new String[columns.length];

        for (int i = 0; i < columns.length; i++) {
            columnsNames[i] = columns[i].name();
        }
    }
}
