package com.mtlk.mtlkdb.expression;

import com.mtlk.mtlkdb.struct.RawRow;

public record Or(Expression left, Expression right) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        return left.evaluate(row) || right.evaluate(row);
    }
}
