package core.query;

import expression.Expression;
import expression.Equals;
import core.Storage;
import expression.And;
import expression.Or;
import struct.Row;
import struct.value.Value;

/**
 * Fluent API for building SELECT queries.
 * 
 * Usage:
 * SelectQuery query = new SelectQuery(storage)
 *     .select("id", "name")
 *     .from("customers")
 *     .where("age", ">", 18)
 *     .and("status", "=", "active");
 * Row[] results = query.execute();
 */
public class SelectQuery {
    private Storage storage;
    private String[] columns;
    private String tableName;
    private Expression whereExpression;

    public SelectQuery(Storage storage) {
        this.storage = storage;
    }

    public SelectQuery select(String... cols) {
        this.columns = cols;
        return this;
    }

    public SelectQuery from(String table) {
        this.tableName = table;
        return this;
    }

    public SelectQuery where(String column, String op, Object value) {
        Expression expr = buildComparisonExpression(column, op, value);
        this.whereExpression = expr;
        return this;
    }

    public SelectQuery and(String column, String op, Object value) {
        if (whereExpression == null) {
            return where(column, op, value);
        }
        Expression rightExpr = buildComparisonExpression(column, op, value);
        this.whereExpression = new And(whereExpression, rightExpr);
        return this;
    }

    public SelectQuery or(String column, String op, Object value) {
        if (whereExpression == null) {
            return where(column, op, value);
        }
        Expression rightExpr = buildComparisonExpression(column, op, value);
        this.whereExpression = new Or(whereExpression, rightExpr);
        return this;
    }

    public Row[] execute() {
        if (tableName == null) {
            throw new IllegalStateException("Table name not specified. Use from(tableName).");
        }
        return storage.select(tableName, columns, whereExpression);
    }

    private Expression buildComparisonExpression(String column, String op, Object value) {
        Value colValue = new Value(column, true);  // true = is column reference
        Value litValue = new Value(String.valueOf(value), false);  // false = is literal

        return switch (op.toLowerCase()) {
            case "=" -> new Equals(colValue, litValue);
            case "!=" -> new expression.NotEquals(colValue, litValue);
            case ">" -> new expression.GreaterThan(colValue, litValue);
            case "<" -> new expression.LessThan(colValue, litValue);
            default -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }
}
