package expression;

import struct.Row;

@FunctionalInterface
public interface Expression {
    boolean evaluate(Row row);
}