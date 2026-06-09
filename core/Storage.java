package core;

import expression.Expression;
import struct.ColumnDefinition;
import struct.RawRow;

public interface Storage {

    public RawRow[] createTable(String tableName, ColumnDefinition[] columns);

    public RawRow[] createDatabase(String dbName);

    public RawRow[] insertRow(String tableName, RawRow row);

    public RawRow[] insertRows(String tableName, RawRow[] rows);

    public RawRow[] deleteRow(String tableName, Object primaryKey);

    public RawRow[] deleteRows(String tableName, Object[] primaryKey);

    public RawRow[] select(String tableName, String[] columns, Expression expression);
}
