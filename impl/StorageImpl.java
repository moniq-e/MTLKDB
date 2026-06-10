package impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import core.Storage;
import core.persistence.Table;
import expression.Expression;
import struct.ColumnDefinition;
import struct.RawRow;

public class StorageImpl implements Storage {
    private HashMap<String, Table> tables;
    private File root;

    public StorageImpl() throws IOException {
        tables = new HashMap<>();
        root = new File("./");

        var existingTables = Arrays
            .stream(root.list((d, fn) -> fn.endsWith(".dat")))
            .map(s -> s.split(".dat")[0])
        .toArray(String[]::new);

        for (var tableName : existingTables) {
            tables.put(tableName, new Table(tableName, null));
        }
    }

    @Override
    public boolean createTable(String tableName, ColumnDefinition[] columns) {
        if (!tables.containsKey(tableName)) {
            try {
                tables.put(tableName, new Table(tableName, columns));
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