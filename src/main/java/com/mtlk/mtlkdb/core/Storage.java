package com.mtlk.mtlkdb.core;

import com.mtlk.mtlkdb.expression.Expression;
import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.RawRow;

public interface Storage {

    public boolean createTable(String tableName, ColumnDefinition[] columns);

    public boolean createDatabase(String dbName);

    public int insertRow(String tableName, RawRow row);

    public int insertRows(String tableName, RawRow[] rows);

    public int deleteRow(String tableName, Object primaryKey);

    public int deleteRows(String tableName, Object[] primaryKey);

    public RawRow[] select(String tableName, String[] columns, Expression expression);
}
