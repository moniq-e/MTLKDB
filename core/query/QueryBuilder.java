package core.query;

import core.Storage;

/**
 * Factory/entry point for building type-safe queries using fluent API.
 * 
 * Usage:
 * 
 * // SELECT
 * Row[] results = QueryBuilder.select(storage)
 *     .select("id", "name")
 *     .from("customers")
 *     .where("age", ">", 18)
 *     .execute();
 * 
 * // INSERT
 * Row[] result = QueryBuilder.insert(storage)
 *     .into("customers")
 *     .columns("id", "name", "age")
 *     .values(1, "Alice", 30)
 *     .execute();
 * 
 * // CREATE TABLE
 * Row[] result = QueryBuilder.createTable(storage)
 *     .table("customers")
 *     .column("id", ColumnType.INT, true)
 *     .column("name", ColumnType.varchar(100), false)
 *     .execute();
 */
public class QueryBuilder {

    private QueryBuilder() {}

    public static SelectQuery select(Storage storage) {
        return new SelectQuery(storage);
    }

    public static InsertQuery insert(Storage storage) {
        return new InsertQuery(storage);
    }

    public static CreateTableQuery createTable(Storage storage) {
        return new CreateTableQuery(storage);
    }
}
