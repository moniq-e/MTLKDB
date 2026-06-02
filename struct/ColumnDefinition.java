package struct;

import org.jetbrains.annotations.Nullable;

public record ColumnDefinition(String name, ColumnType columnType, @Nullable ConstraintMap[] constraints) {}