package com.mtlk.mtlkdb.core.table;

import static com.mtlk.mtlkdb.core.persistence.record.RecordPage.VARCHAR_SIZE_BYTES;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.core.persistence.index.IndexManager;
import com.mtlk.mtlkdb.core.persistence.record.BufferPool;
import com.mtlk.mtlkdb.struct.ColumnType;
import com.mtlk.mtlkdb.struct.RawRow;

public class Table {
    private String tableName;
    private File tableFolder;
    private TableSchema schema;
    private BufferPool pages;
    private IndexManager indexManager;

    public Table(String tableName, File tableFolder) throws IOException {
        this.tableName = tableName;
        this.tableFolder = tableFolder;
        readColumnDefinition();
        pages = new BufferPool(tableName + ".dat");
        indexManager = new IndexManager(tableName + ".idx");
    }

    private void readColumnDefinition() throws IOException {
        var files = tableFolder.listFiles((d, f) -> f.endsWith(".schema"));
        if (files.length < 1) throw new IOException(tableName + " .schema file can't be found.");

        schema = new TableSchema(files[0]);
    }

    public static String extractTableName(File folder) {
        return folder.getName().split("table_")[1];
    }

    @Nullable
    public boolean deleteRow(String primaryKey) throws IOException {
        var type = schema.getPrimaryKey().columnType();

        if (type != ColumnType.INT) return false;

        var value = Integer.parseInt(primaryKey);

        var rid = indexManager.search(value);
        if (rid == null) return false;

        var page = pages.getPage(rid.pageId());
        page.removeRecord(rid.slotId());
        pages.writePage(page);

        return true;
    }

    public RawRow deserializeRow(byte[] record) {
        var rowData = new byte[schema.size()][];

        int k = 0;
        for (int i = 0; i < schema.size(); i++) {
            var colType = schema.get(i).columnType();

            if (colType.isVarchar()) {
                var varsize = (record[k] << 8) | record[k + 1];

                System.arraycopy(record, k + VARCHAR_SIZE_BYTES, rowData[i], 0, varsize);
                k += VARCHAR_SIZE_BYTES + varsize;
            } else {
                System.arraycopy(record, k, rowData[i], 0, colType.getSize());
                k += colType.getSize();
            }
        }
        return new RawRow(schema.getColumnNames(), rowData);
    }
}
