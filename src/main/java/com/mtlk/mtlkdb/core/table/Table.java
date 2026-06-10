package com.mtlk.mtlkdb.core.table;

import java.io.File;
import java.io.IOException;

import com.mtlk.mtlkdb.core.persistence.BufferPool;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.util.Consts;

public class Table {
    private String tableName;
    private File tableFolder;
    private TableSchema schema;
    private BufferPool rows;

    public Table(String tableName, File tableFolder) throws IOException {
        this.tableName = tableName;
        this.tableFolder = tableFolder;
        readColumnDefinition();
        rows = new BufferPool(tableName + ".dat");
    }

    private void readColumnDefinition() throws IOException {
        var files = tableFolder.listFiles((d, f) -> f.endsWith(".schema"));
        if (files.length < 1) throw new IOException(tableName + " .schema file can't be found.");

        schema = new TableSchema(files[0]);
    }

    public static String extractTableName(File folder) {
        return folder.getName().split("table_")[1];
    }

    public RawRow deserializeRow(byte[] record) {
        var rowData = new byte[schema.size()][];

        int k = 0;
        for (int i = 0; i < schema.size(); i++) {
            var colType = schema.get(i).columnType();

            if (colType.isVarchar()) {
                var varsize = (record[k] << 8) | record[k + 1];

                System.arraycopy(record, k + Consts.VARCHAR_SIZE_BYTES, rowData[i], 0, varsize);
                k += Consts.VARCHAR_SIZE_BYTES + varsize;
            } else {
                System.arraycopy(record, k, rowData[i], 0, colType.getSize());
                k += colType.getSize();
            }
        }
        return new RawRow(schema.getColumnNames(), rowData);
    }
}
