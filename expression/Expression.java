package expression;

import struct.RawRow;

@FunctionalInterface
public interface Expression {
    boolean evaluate(RawRow row);
}