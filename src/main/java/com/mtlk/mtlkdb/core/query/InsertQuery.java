package com.mtlk.mtlkdb.core.query;

import java.util.ArrayList;

import com.mtlk.mtlkdb.core.Storage;
import com.mtlk.mtlkdb.struct.rawrow.RawRow;
import com.mtlk.mtlkdb.struct.rawrow.RawRowEncoder;
import com.mtlk.mtlkdb.struct.util.ArrayAsCollection;

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
        values.addAll(ArrayAsCollection.of(vals));
        return this;
    }

    public int execute() {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException("Table name not specified. Use into(tableName).");
        }

        if (values.isEmpty()) {
            throw new IllegalStateException("No values specified. Use values(...).");
        }

        if (values.size() == 1) {
            RawRow row = new RawRow(columns, RawRowEncoder.encodeValues(values.get(0)));
            return storage.insertRow(tableName, row) ? 1 : 0;
        } else {
            var rows = values.stream()
                .map(vals -> new RawRow(columns, RawRowEncoder.encodeValues(vals)))
                .toArray(RawRow[]::new);
            return storage.insertRows(tableName, rows);
        }
    }
}
