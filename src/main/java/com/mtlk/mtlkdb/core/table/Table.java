package com.mtlk.mtlkdb.core.table;

import static com.mtlk.mtlkdb.core.persistence.record.RecordPage.VARCHAR_SIZE_BYTES;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mtlk.mtlkdb.core.persistence.index.IndexManager;
import com.mtlk.mtlkdb.core.persistence.record.BufferPool;
import com.mtlk.mtlkdb.expression.Expression;
import com.mtlk.mtlkdb.struct.ColumnType;
import com.mtlk.mtlkdb.struct.RecordId;
import com.mtlk.mtlkdb.struct.rawrow.RawRow;
import com.mtlk.mtlkdb.struct.util.ArrayAsCollection;
import com.mtlk.mtlkdb.struct.util.Encoder;

public class Table implements Closeable {
    private String tableName;
    private File tableFolder;
    private TableSchema schema;
    private BufferPool pages;
    private IndexManager indexManager;

    public Table(String tableName, File tableFolder) throws IOException {
        this.tableName = tableName;
        this.tableFolder = tableFolder;
        readColumnDefinition();
        pages = new BufferPool(Path.of(tableFolder.toString(), tableName + ".dat"));
        indexManager = new IndexManager(Path.of(tableFolder.toString(), tableName + ".idx"));
    }

    private void readColumnDefinition() throws IOException {
        var files = tableFolder.listFiles((d, f) -> f.endsWith("-schema.json"));
        if (files.length < 1) throw new IOException(tableName + " schema file can't be found.");

        schema = new TableSchema(files[0]);
    }

    public static String extractTableName(File folder) {
        return folder.getName().split("table_")[1];
    }

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

    public int deleteRows(String[] primaryKeys) throws IOException {
        var type = schema.getPrimaryKey().columnType();

        if (type != ColumnType.INT) return 0;

        var rids = new HashMap<Integer, ArrayList<Integer>>(primaryKeys.length);

        for (int i = 0; i < primaryKeys.length; i++) {
            var value = Integer.parseInt(primaryKeys[i]);

            var rid = indexManager.search(value);
            if (rid != null) {
                var slots = rids.get(rid.pageId());
                if (slots == null) slots = new ArrayList<>();
                slots.add(rid.slotId());
            }
        }

        int count = 0;
        for (int pageId : rids.keySet()) {
            var page = pages.getPage(pageId);
            for (int slotId : rids.get(pageId)) {
                page.removeRecord(slotId);
                count++;
            }
            pages.writePage(page);
        }
        return count;
    }

    public RawRow[] select(String[] columns, Expression expression) throws IOException {
        List<RecordId> ridsToFetch = new ArrayList<>();
        var resultRows = new ArrayList<RawRow>();

        if (expression != null && expression.referPrimaryKey(schema)) {
            var sr = expression.getScanRange();

            if (sr.getSpecificIds() != null && !sr.getSpecificIds().isEmpty()) {
                for (int id : sr.getSpecificIds()) {
                    var rid = indexManager.search(id);
                    if (rid != null) ridsToFetch.add(rid);
                }
            } else {
                var rangeResult = indexManager.search(sr.getLow(), sr.getHigh());
                if (rangeResult.length > 0) {
                    ridsToFetch.addAll(ArrayAsCollection.of(rangeResult));
                }
            }

            ridsToFetch = ridsToFetch.stream().distinct().sorted().toList();

            for (var rid : ridsToFetch) {
                var recordPage = pages.getPage(rid.pageId());
                var recordBytes = recordPage.getRecord(rid.slotId());

                if (recordBytes == null) continue;

                var row = deserializeRow(recordBytes);
                if (expression.evaluate(row)) resultRows.add(row);
            }
            return resultRows.toArray(RawRow[]::new);
        }

        int totalPages = pages.getTotalPages();
        for (int pageId = 0; pageId < totalPages; pageId++) {
            var recordPage = pages.getPage(pageId);
            var allRecords = recordPage.getRecords();

            for (var recordBytes : allRecords) {
                var row = deserializeRow(recordBytes);
                if (expression == null || expression.evaluate(row)) resultRows.add(row);
            }
        }

        return resultRows.toArray(RawRow[]::new);
    }

    public boolean insert(RawRow row) throws IOException {
        var rid = pages.insertRecord(row.serialize());

        var pk = schema.getPrimaryKey();
        if (pk.columnType() == ColumnType.INT) {
            indexManager.insert(Encoder.decodeInt(row.getValue(pk.name())), rid);
        }
        return true;
    }

    public int insert(RawRow[] rows) {
        var count = 0;

        for (int i = 0; i < rows.length; i++) {
            try {
                if (insert(rows[i])) count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
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
                rowData[i] = new byte[colType.getSize()];
                System.arraycopy(record, k, rowData[i], 0, colType.getSize());
                k += colType.getSize();
            }
        }
        return new RawRow(schema.getColumnNames(), rowData);
    }

    @Override
    public void close() throws IOException {
        indexManager.close();
        pages.close();
    }
}
