package com.mtlk.mtlkdb.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.mtlk.mtlkdb.core.Storage;
import com.mtlk.mtlkdb.core.table.Table;
import com.mtlk.mtlkdb.expression.Expression;
import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.RawRow;

public class StorageImpl implements Storage {
    private HashMap<String, Table> tables;
    private File root;

    public StorageImpl() throws IOException {
        tables = new HashMap<>();
        root = new File("./");

        var existingTables = root.listFiles((d, fn) -> d.getName().startsWith("table_"));

        for (var tableFolder : existingTables) {
            var tableName = Table.extractTableName(tableFolder);
            tables.put(tableName, new Table(tableName, tableFolder));
        }
    }

    @Override
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

    @Override
    public boolean createDatabase(String dbName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDatabase'");
    }

    @Override
    public int insertRow(String tableName, RawRow row) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRow'");
    }

    @Override
    public int insertRows(String tableName, RawRow[] rows) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRows'");
    }

    @Override
    public int deleteRow(String tableName, Object primaryKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRow'");
    }

    @Override
    public int deleteRows(String tableName, Object[] primaryKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRows'");
    }

    @Override
    public RawRow[] select(String tableName, String[] columns, Expression expression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'select'");
    }
}