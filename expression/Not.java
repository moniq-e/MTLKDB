package expression;

import struct.RawRow;

public record Not(Expression expression) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        return !expression.evaluate(row);
    }
}
