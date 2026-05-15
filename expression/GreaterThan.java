package expression;

import struct.Row;

public record GreaterThan(String columnName, Comparable<Object> value) implements Expression {

    @Override
    public boolean evaluate(Row row) {
        if (value == null) {
            return false;
        }

        return value.compareTo(row.getValue(columnName)) < 0;
    }
}