package expression;

import struct.Row;

public record Not(Expression expression) implements Expression {

    @Override
    public boolean evaluate(Row row) {
        return !expression.evaluate(row);
    }
}
