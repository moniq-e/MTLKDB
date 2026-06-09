package expression;

import struct.RawRow;
import struct.value.LiteralValue;

public record NotEquals(LiteralValue a, LiteralValue b) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        var aValue = a.evaluate(row);
        var bValue = b.evaluate(row);

        return aValue.compareTo(bValue) != 0;
    }
}