package expression;

import struct.RawRow;

public record Or(Expression left, Expression right) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        return left.evaluate(row) || right.evaluate(row);
    }
}
