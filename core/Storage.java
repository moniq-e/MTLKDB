package core;

import expression.Expression;
import struct.Column;
import struct.Row;

public interface Storage {

    public Row[] createTable(String tableName, Column[] columns);

    public Row[] createDatabase(String dbName);

    public Row[] insertRow(String tableName, Row row);

    public Row[] insertRows(String tableName, Row[] rows);

    public Row[] deleteRow(String tableName, Object primaryKey);

    public Row[] deleteRows(String tableName, Object[] primaryKey);

    public Row[] select(String tableName, String[] columns, Expression expression);
}
