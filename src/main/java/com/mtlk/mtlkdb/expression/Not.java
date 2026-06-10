package com.mtlk.mtlkdb.expression;

import com.mtlk.mtlkdb.struct.RawRow;

public record Not(Expression expression) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        return !expression.evaluate(row);
    }
}
