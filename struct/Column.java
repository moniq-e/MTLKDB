package struct;

public record Column(String name, ColumnType columnType, ConstraintMap[] constraints) {}