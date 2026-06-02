package core.query;

import core.Storage;

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
