package com.mtlk.mtlkdb.core.query;

import java.util.ArrayList;

import com.mtlk.mtlkdb.core.Storage;
import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.ColumnType;
import com.mtlk.mtlkdb.struct.ConstraintMap;

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

    public boolean execute() {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException("Table name not specified. Use table(tableName).");
        }

        if (columns.isEmpty()) {
            throw new IllegalStateException("No columns specified. Use column(...).");
        }

        return storage.createTable(tableName, columns.toArray(ColumnDefinition[]::new));
    }
}
