package core;

import expression.Expression;
import struct.ColumnDefinition;
import struct.RawRow;

public interface Storage {

    public boolean createTable(String tableName, ColumnDefinition[] columns);

    public boolean createDatabase(String dbName);

    public int insertRow(String tableName, RawRow row);

    public int insertRows(String tableName, RawRow[] rows);

    public int deleteRow(String tableName, Object primaryKey);

    public int deleteRows(String tableName, Object[] primaryKey);

    public RawRow[] select(String tableName, String[] columns, Expression expression);
}
