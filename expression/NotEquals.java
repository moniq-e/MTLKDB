package expression;

import struct.Row;

public record NotEquals(String columnName, Object value) implements Expression {

    @Override
    public boolean evaluate(Row row) {
        if (row.getValue(columnName) == null) {
            return value != null;
        }

        return !row.getValue(columnName).equals(value);
    }
}