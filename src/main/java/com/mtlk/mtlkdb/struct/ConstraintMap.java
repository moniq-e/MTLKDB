package com.mtlk.mtlkdb.struct;

import org.jetbrains.annotations.Nullable;

public class ConstraintMap {
    private static final ConstraintMap PRIMARY = new ConstraintMap(ConstraintType.PRIMARY, null);

    private ConstraintType constraint;
    @Nullable
    private Object value;

    private ConstraintMap(ConstraintType constraint, Object value) {
        this.constraint = constraint;
        this.value = value;
    }

    public static ConstraintMap from(String constraintType, @Nullable Object value) {
        var type = ConstraintType.valueOf(constraintType.toUpperCase());

        switch (type) {
            case PRIMARY:
                return PRIMARY;
            case DEFAULT:
                return DEFAULT(value);
            default:
                throw new IllegalArgumentException("Unknown constraint type: " + constraintType);
        }
    }

    public static ConstraintMap PRIMARY() {
        return PRIMARY;
    }

    public static ConstraintMap DEFAULT(@Nullable Object value) {
        return new ConstraintMap(ConstraintType.DEFAULT, value);
    }

    public ConstraintType getConstraint() {
        return constraint;
    }

    public Object getValue() {
        return value;
    }

    private enum ConstraintType {
        PRIMARY,
        DEFAULT
    }
}