import struct.Column;
import struct.Row;

public interface Storage {
    
    public void createTable(String tableName, Column[] columns);

    public void insertRow(String tableName, Row row);

    public void insertRows(String tableName, Row[] rows);

    public void deleteRow(String tableName, Object primaryKey);

    public void deleteRows(String tableName, Object[] primaryKey);

    public Row[] select();
}
