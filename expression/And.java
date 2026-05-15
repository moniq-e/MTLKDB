package expression;

import struct.Row;

public record And(Expression left, Expression right) implements Expression {

    @Override
    public boolean evaluate(Row row) {
        return left.evaluate(row) && right.evaluate(row);
    }
}
