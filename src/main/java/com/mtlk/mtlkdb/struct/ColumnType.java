package com.mtlk.mtlkdb.struct;

public class ColumnType {
    private final String name;
    private final Class<?> dataType;
    private final int size;

    public static final ColumnType INT = new ColumnType("INT", Integer.class, 4);
    public static final ColumnType LONG = new ColumnType("LONG", Long.class, 8);
    public static final ColumnType FLOAT = new ColumnType("FLOAT", Float.class, 4);
    public static final ColumnType DOUBLE = new ColumnType("DOUBLE", Double.class, 8);

    private ColumnType(String name, Class<?> dataType, int size) {
        this.name = name;
        this.dataType = dataType;
        this.size = size;
    }

    public static ColumnType varchar(int size) {
        return new ColumnType("VARCHAR", String.class, size);
    }

    public static ColumnType fromString(String typeName, int size) {
        if (typeName.equalsIgnoreCase("VARCHAR")) {
            return varchar(size);
        }
        return fromString(typeName);
    }

    private static ColumnType fromString(String typeName) {
        switch (typeName.toUpperCase()) {
            case "INT":
                return INT;
            case "LONG":
                return LONG;
            case "FLOAT":
                return FLOAT;
            case "DOUBLE":
                return DOUBLE;
            default:
                throw new IllegalArgumentException("Unknown column type: " + typeName);
        }
    }

    public boolean isVarchar() {
        return getName().equals("VARCHAR");
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getDataType() {
        return (Class<T>) dataType;
    }
}
