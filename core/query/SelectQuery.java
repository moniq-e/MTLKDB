package core.query;

import core.Storage;
import expression.And;
import expression.Expression;
import expression.Or;
import struct.Row;

public class SelectQuery {
    private Storage storage;
    private String[] columns;
    private String tableName;
    private Expression whereExpression;

    public SelectQuery(Storage storage) {
        this.storage = storage;
    }

    public SelectQuery columns(String... cols) {
        this.columns = cols;
        return this;
    }

    public SelectQuery from(String table) {
        this.tableName = table;
        return this;
    }

    public SelectQuery where(Expression expression) {
        this.whereExpression = expression;
        return this;
    }

    public SelectQuery and(Expression expression) {
        if (whereExpression == null) {
            return where(expression);
        }

        this.whereExpression = new And(this.whereExpression, expression);
        return this;
    }

    public SelectQuery or(Expression expression) {
        if (whereExpression == null) {
            return where(expression);
        }

        this.whereExpression = new Or(this.whereExpression, expression);
        return this;
    }

    public Row[] execute() {
        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException("Table name not specified. Use from(tableName).");
        }
        return storage.select(tableName, columns, whereExpression);
    }
}
