package core.query;

import java.util.ArrayList;
import java.util.List;

import core.Storage;
import struct.Row;
import struct.ColumnDefinition;

/**
 * Fluent API for building INSERT queries.
 * 
 * Usage:
 * InsertQuery query = new InsertQuery(storage)
 *     .into("customers")
 *     .columns("id", "name", "age")
 *     .values(1, "Alice", 30)
 *     .values(2, "Bob", 25);
 * Row[] result = query.execute();
 */
public class InsertQuery {
    private Storage storage;
    private String tableName;
    private String[] columns;
    private List<Object[]> valuesList;

    public InsertQuery(Storage storage) {
        this.storage = storage;
        this.valuesList = new ArrayList<>();
    }

    public InsertQuery into(String table) {
        this.tableName = table;
        return this;
    }

    public InsertQuery columns(String... cols) {
        this.columns = cols;
        return this;
    }

    public InsertQuery values(Object... vals) {
        valuesList.add(vals);
        return this;
    }

    public Row[] execute() {
        if (tableName == null) {
            throw new IllegalStateException("Table name not specified. Use into(tableName).");
        }
        if (valuesList.isEmpty()) {
            throw new IllegalStateException("No values specified. Use values(...).");
        }

        if (valuesList.size() == 1) {
            Row row = new Row(columns, valuesList.get(0));
            return storage.insertRow(tableName, row);
        } else {
            Row[] rows = valuesList.stream()
                    .map(vals -> new Row(columns, vals))
                    .toArray(Row[]::new);
            return storage.insertRows(tableName, rows);
        }
    }
}
