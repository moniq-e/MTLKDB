package core.query;

import java.util.ArrayList;

import core.Storage;
import struct.Row;
import struct.value.ByteBufferEncoder;
import struct.ArrayAsCollection;

public class InsertQuery {
    private Storage storage;
    private String tableName;
    private String[] columns;
    private ArrayList<String[]> values;

    public InsertQuery(Storage storage) {
        this.storage = storage;
        this.values = new ArrayList<>();
    }

    public InsertQuery into(String table) {
        this.tableName = table;
        return this;
    }

    public InsertQuery columns(String... cols) {
        this.columns = cols;
        return this;
    }

    public InsertQuery values(String... vals) {
        values.add(vals);
        return this;
    }

    public InsertQuery values(String[]... vals) {
        values.addAll(new ArrayAsCollection<>(vals));
        return this;
    }

    public Row[] execute() {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException("Table name not specified. Use into(tableName).");
        }

        if (values.isEmpty()) {
            throw new IllegalStateException("No values specified. Use values(...).");
        }

        if (values.size() == 1) {
            Row row = new Row(columns, ByteBufferEncoder.encodeValues(values.get(0)));
            return storage.insertRow(tableName, row);
        } else {
            var rows = values.stream()
                .map(vals -> new Row(columns, ByteBufferEncoder.encodeValues(vals)))
                .toArray(Row[]::new);
            return storage.insertRows(tableName, rows);
        }
    }
}
