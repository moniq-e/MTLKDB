package com.mtlk.mtlkdb.expression;

import java.util.ArrayList;

import com.mtlk.mtlkdb.core.table.TableSchema;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.util.ArrayAsCollection;
import com.mtlk.mtlkdb.struct.util.ScanRange;

public record Or(Expression left, Expression right) implements Expression {

    @Override
    public boolean evaluate(RawRow row) {
        return left.evaluate(row) || right.evaluate(row);
    }

    @Override
    public boolean referPrimaryKey(TableSchema schema) {
        return referPrimaryKey(schema, left, right);
    }

    @Override
    public ScanRange getScanRange() { //TODO rever logica
        var ids = new ArrayList<Integer>();

        var leftRange = left.getScanRange();
        var rightRange = right.getScanRange();
        
        if (leftRange.isSpecificLookups()) ids.addAll(new ArrayAsCollection<>(leftRange.getSpecificIds()));
        else if (left.getScanRange().getLow() != null) ids.add(leftRange.getLow()); // se for Equals
        
        if (rightRange.isSpecificLookups()) ids.addAll(new ArrayAsCollection<>(rightRange.getSpecificIds()));
        else if (rightRange.getLow() != null) ids.add(rightRange.getLow());

        return new ScanRange(ids.toArray(new Integer[ids.size()]));
    }
}
