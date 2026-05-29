package core.query;

import java.util.ArrayList;
import java.util.List;

import core.Storage;
import struct.ColumnDefinition;
import struct.ColumnType;
import struct.ConstraintMap;
import struct.Constraints;

/**
 * Fluent API for building CREATE TABLE queries.
 * 
 * Usage:
 * CreateTableQuery query = new CreateTableQuery(storage)
 *     .table("customers")
 *     .column("id", ColumnType.INT, true)  // true = PRIMARY KEY
 *     .column("name", ColumnType.varchar(100), false)
 *     .column("age", ColumnType.INT, false);
 * Row[] result = query.execute();
 */
public class CreateTableQuery {
    private Storage storage;
    private String tableName;
    private List<ColumnDefinition> columns;

    public CreateTableQuery(Storage storage) {
        this.storage = storage;
        this.columns = new ArrayList<>();
    }

    public CreateTableQuery table(String name) {
        this.tableName = name;
        return this;
    }

    public CreateTableQuery column(String name, ColumnType type, boolean isPrimary) {
        ConstraintMap[] constraints = null;
        if (isPrimary) {
            constraints = new ConstraintMap[] { new ConstraintMap(Constraints.PRIMARY, null) };
        }
        columns.add(new ColumnDefinition(name, type, constraints));
        return this;
    }

    public CreateTableQuery column(String name, ColumnType type) {
        return column(name, type, false);
    }

    public Row[] execute() {
        if (tableName == null) {
            throw new IllegalStateException("Table name not specified. Use table(tableName).");
        }
        if (columns.isEmpty()) {
            throw new IllegalStateException("No columns specified. Use column(...).");
        }

        return storage.createTable(tableName, columns.toArray(new ColumnDefinition[0]));
    }
}
