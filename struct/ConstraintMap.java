package struct;

public class ConstraintMap {
    private static final ConstraintMap PRIMARY = new ConstraintMap(ConstraintType.PRIMARY, null);

    private ConstraintType constraint;
    private Object value;

    private ConstraintMap(ConstraintType constraint, Object value) {
        this.constraint = constraint;
        this.value = value;
    }

    public static ConstraintMap PRIMARY() {
        return PRIMARY;
    }

    public static ConstraintMap DEFAULT(Object value) {
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