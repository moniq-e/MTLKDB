package com.mtlk.mtlkdb.expression;

import java.util.Arrays;

import com.mtlk.mtlkdb.core.table.TableSchema;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.util.ScanRange;
import com.mtlk.mtlkdb.struct.value.LiteralValue;

public class In implements Expression {
    private LiteralValue a;
    private LiteralValue[] range;

    public In(LiteralValue a, LiteralValue... range) {
        this.a = a;
        this.range = range;
    }

    public In(LiteralValue a, boolean sorted, LiteralValue... range) {
        this(a, range);
        if (!sorted) Arrays.sort(this.range);
    }

    @Override
    public boolean evaluate(RawRow row) {
        var aValue = a.evaluate(row);
        return Arrays.binarySearch(range, aValue) >= 0;
    }

    @Override
    public boolean referPrimaryKey(TableSchema schema) {
        return referPrimaryKey(schema, a);
    }

    @Override
    public ScanRange getScanRange() {
        return new ScanRange(Arrays.stream(range).map(lv -> (Integer) lv.asInt()).toArray(a -> new Integer[a]));
    }
}