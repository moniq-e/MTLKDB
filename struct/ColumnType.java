package struct;
public enum ColumnType {
    VARCHAR(String.class),
    INT(Integer.class, 4),
    LONG(Long.class, 8),
    FLOAT(Float.class, 4),
    DOUBLE(Double.class, 8);

    private int size;
    private Class<?> dataType;

    ColumnType(Class<?> dataType) {
        this.dataType = dataType;
    }

    ColumnType(Class<?> dataType, int size) {
        this.dataType = dataType;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getDataType() {
        return (Class<T>) dataType;
    }
}
