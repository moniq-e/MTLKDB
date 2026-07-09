package com.mtlk.mtlkdb.expression;

import com.mtlk.mtlkdb.core.table.TableSchema;
import com.mtlk.mtlkdb.struct.rawrow.RawRow;
import com.mtlk.mtlkdb.struct.util.ScanRange;

public record Not(Expression expression) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        return !expression.evaluate(row);
    }

    @Override
    public boolean referPrimaryKey(TableSchema schema) {
        return referPrimaryKey(schema, expression);
    }

    @Override
    public ScanRange getScanRange() {
        return expression.getScanRange();
    }
}
