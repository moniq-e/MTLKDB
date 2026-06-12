package com.mtlk.mtlkdb.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.mtlk.mtlkdb.core.table.Table;
import com.mtlk.mtlkdb.expression.Expression;
import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.RawRow;

public class Storage {
private HashMap<String, Table> tables;
    private File root;

    public Storage() throws IOException {
        tables = new HashMap<>();
        root = new File("./");

        var existingTables = root.listFiles((d, fn) -> d.getName().startsWith("table_"));

        for (var tableFolder : existingTables) {
            var tableName = Table.extractTableName(tableFolder);
            tables.put(tableName, new Table(tableName, tableFolder));
        }
    }

    public boolean createTable(String tableName, ColumnDefinition[] columns) {
        if (!tables.containsKey(tableName)) {
            try {
                var newTableFolder = new File(root, "table_" + tableName);
                tables.put(tableName, new Table(tableName, newTableFolder));
            } catch (IOException e) {
                return false;
            }
            return true;
        } else return false;
    }

    public boolean createDatabase(String dbName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDatabase'");
    }

    public int insertRow(String tableName, RawRow row) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRow'");
    }

    public int insertRows(String tableName, RawRow[] rows) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRows'");
    }

    public int deleteRow(String tableName, Object primaryKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRow'");
    }

    public int deleteRows(String tableName, Object[] primaryKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRows'");
    }

    public RawRow[] select(String tableName, String[] columns, Expression expression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'select'");
    }
}
