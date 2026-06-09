package impl;

import java.io.IOException;

import core.Storage;
import core.persistence.DiskManager;
import expression.Expression;
import struct.ColumnDefinition;
import struct.RawRow;

public class StorageImpl implements Storage {
    private DiskManager db;

    public StorageImpl() throws IOException {
        db = new DiskManager("default");
    }

    @Override
    public RawRow[] createTable(String tableName, ColumnDefinition[] columns) {
        if (db.createTable(tableName, columns)) {
            return new RawRow[1];
        } else return new RawRow[0];
    }

    @Override
    public RawRow[] createDatabase(String dbName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDatabase'");
    }

    @Override
    public RawRow[] insertRow(String tableName, RawRow row) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRow'");
    }

    @Override
    public RawRow[] insertRows(String tableName, RawRow[] rows) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRows'");
    }

    @Override
    public RawRow[] deleteRow(String tableName, Object primaryKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRow'");
    }

    @Override
    public RawRow[] deleteRows(String tableName, Object[] primaryKey) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteRows'");
    }

    @Override
    public RawRow[] select(String tableName, String[] columns, Expression expression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'select'");
    }
}