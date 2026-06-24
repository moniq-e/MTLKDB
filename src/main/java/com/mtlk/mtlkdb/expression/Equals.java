package com.mtlk.mtlkdb.expression;

import org.jetbrains.annotations.Nullable;

import com.mtlk.mtlkdb.core.table.TableSchema;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.util.ScanRange;
import com.mtlk.mtlkdb.struct.value.ColumnValue;
import com.mtlk.mtlkdb.struct.value.LiteralValue;

public record Equals(ColumnValue a, LiteralValue b) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        var aValue = a.evaluate(row);
        var bValue = b.evaluate(row);

        return aValue.compareTo(bValue) == 0;
    }

    @Override
    public boolean referPrimaryKey(TableSchema schema) {
        return referPrimaryKey(schema, a, b);
    }

    @Override
    @Nullable
    public ScanRange getScanRange() {
        return b instanceof ColumnValue ? null : new ScanRange(b.asInt());
    }
}