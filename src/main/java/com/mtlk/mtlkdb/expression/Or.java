package com.mtlk.mtlkdb.expression;

import com.mtlk.mtlkdb.core.table.TableSchema;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.util.ScanRange;

public record Or(Expression left, Expression right) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        return left.evaluate(row) || right.evaluate(row);
    }

    @Override
    public boolean referPrimaryKey(TableSchema schema) {
        return referPrimaryKey(schema, left, right);
    }

    @Override
    public ScanRange getScanRange() {
        var leftRange = left.getScanRange();
        var rightRange = right.getScanRange();

        return ScanRange.merge(leftRange, rightRange);
    }
}
