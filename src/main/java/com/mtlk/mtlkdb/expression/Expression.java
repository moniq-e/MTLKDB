package com.mtlk.mtlkdb.expression;

import com.mtlk.mtlkdb.struct.RawRow;

@FunctionalInterface
public interface Expression {
    boolean evaluate(RawRow row);
}