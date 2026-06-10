package com.mtlk.mtlkdb.expression;

import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.value.LiteralValue;

public record GreaterThan(LiteralValue a, LiteralValue b) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        var aValue = a.evaluate(row);
        var bValue = b.evaluate(row);

        return aValue.compareTo(bValue) > 0;
    }
}