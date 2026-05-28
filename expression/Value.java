package expression;

import struct.Row;

public record Value(String value, boolean isColumn) implements Comparable<Value> {

    Object evaluate(Row row) {
        return isColumn ? row.getValue(value) : value;
    }

    @Override
    public int compareTo(Value o) {
        var comp = 
        return isColumn ? row.getValue(value) : value;
    }

    private Comparable<?> convertStringToComparable(String value) throws NumberFormatException {
        try {
            if (!value.contains(".")) {
                try {
                    return Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    return Long.valueOf(value);
                }
            } else {
                try {
                    return Float.valueOf(value);
                } catch (NumberFormatException e) {
                    return Double.valueOf(value);
                }
            }
        } catch (Exception e) {
            return value;
        }
    }
}
