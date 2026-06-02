package expression;

import struct.Row;
import struct.value.LiteralValue;

public record NotEquals(LiteralValue a, LiteralValue b) implements Expression {

    @Override
    public boolean evaluate(Row row) {
        var aValue = a.evaluate(row);
        var bValue = b.evaluate(row);

        return aValue.compareTo(bValue) != 0;
    }
}