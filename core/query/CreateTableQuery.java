package core.query;

import java.util.ArrayList;

import core.Storage;
import struct.ColumnDefinition;
import struct.ColumnType;
import struct.ConstraintMap;
import struct.RawRow;

public class CreateTableQuery {
    private Storage storage;
    private String tableName;
    private ArrayList<ColumnDefinition> columns;

    public CreateTableQuery(Storage storage) {
        this.storage = storage;
        this.columns = new ArrayList<>();
    }

    public CreateTableQuery table(String name) {
        this.tableName = name;
        return this;
    }

    public CreateTableQuery column(String name, ColumnType type, ConstraintMap... constraints) {
        columns.add(new ColumnDefinition(name, type, constraints));
        return this;
    }

    public CreateTableQuery column(String name, ColumnType type) {
        return column(name, type, (ConstraintMap) null);
    }

    public RawRow[] execute() {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException("Table name not specified. Use table(tableName).");
        }

        if (columns.isEmpty()) {
            throw new IllegalStateException("No columns specified. Use column(...).");
        }

        return storage.createTable(tableName, columns.toArray(new ColumnDefinition[columns.size()]));
    }
}
