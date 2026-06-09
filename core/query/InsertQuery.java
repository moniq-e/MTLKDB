package core.query;

import java.util.ArrayList;

import core.Storage;
import struct.RawRow;
import struct.util.ArrayAsCollection;
import struct.util.RawEncoder;

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

    public RawRow[] execute() {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException("Table name not specified. Use into(tableName).");
        }

        if (values.isEmpty()) {
            throw new IllegalStateException("No values specified. Use values(...).");
        }

        if (values.size() == 1) {
            RawRow row = new RawRow(columns, RawEncoder.encodeValues(values.get(0)));
            return storage.insertRow(tableName, row);
        } else {
            var rows = values.stream()
                .map(vals -> new RawRow(columns, RawEncoder.encodeValues(vals)))
                .toArray(RawRow[]::new);
            return storage.insertRows(tableName, rows);
        }
    }
}
