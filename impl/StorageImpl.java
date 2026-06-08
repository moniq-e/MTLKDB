package impl;

import java.io.IOException;

import core.Storage;
import core.persistence.DiskManager;
import expression.Expression;
import struct.ColumnDefinition;
import struct.Row;

public class StorageImpl implements Storage {
    private DiskManager db;

    public StorageImpl() throws IOException {
        db = new DiskManager("default");
    }

    @Override
    public Row[] createTable(String tableName, ColumnDefinition[] columns) {
        if (db.createTable(tableName, columns)) {
            return new Row[1];
        } else return new Row[0];
    }

    @Override
    public Row[] createDatabase(String dbName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDatabase'");
    }

    @Override
    public Row[] insertRow(String tableName, Row row) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRow'");
    }

    @Override
    public Row[] insertRows(String tableName, Row[] rows) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRows'");
    }

    @Override
    public Row[] deleteRow(String tableName, Object primaryKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRow'");
    }

    @Override
    public Row[] deleteRows(String tableName, Object[] primaryKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRows'");
    }

    @Override
    public Row[] select(String tableName, String[] columns, Expression expression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'select'");
    }
}