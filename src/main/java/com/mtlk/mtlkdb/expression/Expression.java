package com.mtlk.mtlkdb.expression;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.core.table.TableSchema;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.util.ScanRange;
import com.mtlk.mtlkdb.struct.value.ColumnValue;
import com.mtlk.mtlkdb.struct.value.LiteralValue;

public interface Expression {

    public boolean evaluate(RawRow row);

    public boolean referPrimaryKey(TableSchema schema);

    @Nullable
    public ScanRange getScanRange();

    public default boolean referPrimaryKey(TableSchema schema, LiteralValue a, LiteralValue b) {
        return referPrimaryKey(schema, a) || referPrimaryKey(schema, b);
    }

    public default boolean referPrimaryKey(TableSchema schema, LiteralValue lv) {
        if (lv instanceof ColumnValue) {
            var ac = (ColumnValue) lv;
            return ac.getColumnName().equals(schema.getPrimaryKey().name());
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