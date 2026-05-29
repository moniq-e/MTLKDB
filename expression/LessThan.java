package expression;

import struct.Row;
import struct.value.Value;

public record LessThan(Value a, Value b) implements Expression {

    @Override
    public boolean evaluate(Row row) {
        var aValue = a.evaluate(row);
        var bValue = b.evaluate(row);

        return aValue.compareTo(bValue) > 0;
    }
}