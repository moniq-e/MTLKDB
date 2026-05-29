package core;

import expression.Expression;
import struct.ColumnDefinition;
import struct.Row;

public interface Storage {

    public Row[] createTable(String tableName, ColumnDefinition[] columns);

    public Row[] createDatabase(String dbName);

    public Row[] insertRow(String tableName, Row row);

    public Row[] insertRows(String tableName, Row[] rows);

    public Row[] deleteRow(String tableName, Object primaryKey);

    public Row[] deleteRows(String tableName, Object[] primaryKey);

    public Row[] select(String tableName, String[] columns, Expression expression);
}
