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
                newTableFolder.mkdir();
                tables.put(tableName, new Table(tableName, newTableFolder));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean createDatabase(String dbName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDatabase'");
    }

    public boolean insertRow(String tableName, RawRow row) {
        var table = findTable(tableName);
        try {
            return table.insert(row);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //TODO estudar bulk insert
    public int insertRows(String tableName, RawRow[] rows) {
        var table = findTable(tableName);
        return table.insert(rows);
    }

    public boolean deleteRow(String tableName, String primaryKey) {
        var table = findTable(tableName);
        try {
            return table.deleteRow(primaryKey);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int deleteRows(String tableName, String[] primaryKey) {
        var table = findTable(tableName);
        try {
            return table.deleteRows(primaryKey);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public RawRow[] select(String tableName, String[] columns, Expression expression) {
        var table = findTable(tableName);
        try {
            return table.select(columns, expression);
        } catch (Exception e) {
            e.printStackTrace();
            return new RawRow[0];
        }
    }

    private Table findTable(String tableName) {
        if (!tables.containsKey(tableName)) {
            throw new IllegalArgumentException("Could not find "+tableName+" table.");
        }
        return tables.get(tableName);
    }

    public void closeTable(String tableName) {
        try {
            findTable(tableName).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
