package expression;

import struct.Row;

public record NotEquals(Value a, Value b) implements Expression {

    @Override
    public boolean evaluate(Row row) {
        var aValue = a.evaluate(row);
        var bValue = b.evaluate(row);

        if (aValue == null) {
            return bValue != null;
        }

        return !aValue.equals(bValue);
    }
}