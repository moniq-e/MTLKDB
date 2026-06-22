package com.mtlk.mtlkdb.expression;

import com.mtlk.mtlkdb.core.table.TableSchema;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.value.ColumnValue;
import com.mtlk.mtlkdb.struct.value.LiteralValue;

public interface Expression {

    public boolean evaluate(RawRow row);

    public boolean referPrimaryKey(TableSchema schema);

    public default boolean referPrimaryKey(TableSchema schema, LiteralValue a, LiteralValue b) {
        if (a instanceof ColumnValue) {
            var ac = (ColumnValue) a;
            return ac.getColumnName().equals(schema.getPrimaryKey().name());
        } else if (b instanceof ColumnValue) {
            var bc = (ColumnValue) b;
            return bc.getColumnName().equals(schema.getPrimaryKey().name());
        }
        return false;
    }

    public default boolean referPrimaryKey(TableSchema schema, Expression left, Expression right) {
        return left.referPrimaryKey(schema) || right.referPrimaryKey(schema);
    }

    public default boolean referPrimaryKey(TableSchema schema, Expression expression) {
        return expression.referPrimaryKey(schema);
    }
}