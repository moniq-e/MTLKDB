package com.mtlk.mtlkdb.expression;

import com.mtlk.mtlkdb.core.table.TableSchema;
import com.mtlk.mtlkdb.struct.rawrow.RawRow;
import com.mtlk.mtlkdb.struct.util.ScanRange;

public record And(Expression left, Expression right) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        return left.evaluate(row) && right.evaluate(row);
    }

    @Override
    public boolean referPrimaryKey(TableSchema schema) {
        return referPrimaryKey(schema, left, right);
    }

    @Override
    public ScanRange getScanRange() {
        return ScanRange.mergeAnd(left.getScanRange(), right.getScanRange());
    }
}
