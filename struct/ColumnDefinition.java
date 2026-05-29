package struct;

public record ColumnDefinition(String name, ColumnType columnType, ConstraintMap[] constraints) {}