package test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import core.Storage;
import core.query.QueryBuilder;
import exception.InvalidSyntaxException;
import expression.Equals;
import struct.ColumnDefinition;
import struct.ColumnType;
import struct.ConstraintMap;
import struct.Row;
import struct.value.LiteralValue;

public class CLITest {
    private Storage storage = new Storage() {
        @Override
        public Row[] createTable(String tableName, ColumnDefinition[] columns) {
            return new Row[0];
        }

        @Override
        public Row[] createDatabase(String dbName) {
            return new Row[0];
        }

        @Override
        public Row[] insertRow(String tableName, Row row) {
            return new Row[] { row };
        }

        @Override
        public Row[] insertRows(String tableName, Row[] rows) {
            return new Row[0];
        }

        @Override
        public Row[] deleteRow(String tableName, Object primaryKey) {
            return new Row[0];
        }

        @Override
        public Row[] deleteRows(String tableName, Object[] primaryKeys) {
            return new Row[0];
        }

        @Override
        public Row[] select(String tableName, String[] columns, expression.Expression expression) {
            return new Row[0];
        }
    };
    private Row[] emptyArray = new Row[0];

    @Test
    public void testInsert() throws InvalidSyntaxException {
        assertArrayEquals(emptyArray, QueryBuilder.insert(storage)
            .into("customers")
            .values("a", "b", "c")
            .values("d", "e", "f")
        .execute());
    }

    @Test()
    public void testSelect() throws InvalidSyntaxException {
        assertArrayEquals(emptyArray, QueryBuilder.select(storage)
            .columns("a", "b", "c")
            .from("customers")
        .execute());

        assertArrayEquals(emptyArray, QueryBuilder.select(storage)
            .columns("*")
            .from("customers")
        .execute());

        assertArrayEquals(emptyArray, QueryBuilder.select(storage)
            .columns("*")
            .from("customers")
            .where(new Equals(new LiteralValue("a"), new LiteralValue("b")))
        .execute());

        assertArrayEquals(emptyArray, QueryBuilder.select(storage)
            .columns("*")
            .from("customers")
            .where(new Equals(new LiteralValue("a"), new LiteralValue("b")))
            .and(row -> true)
        .execute());
    }

    @Test
    public void testCreateTable() throws InvalidSyntaxException {
        assertArrayEquals(emptyArray, QueryBuilder.createTable(storage)
            .table("customers")
            .column("a", ColumnType.INT)
            .column("b", ColumnType.INT)
            .column("c", ColumnType.INT)
        .execute());

        assertArrayEquals(emptyArray, QueryBuilder.createTable(storage)
            .table("customers")
            .column("a", ColumnType.INT)
            .column("b", ColumnType.varchar(255), ConstraintMap.PRIMARY())
            .column("c", ColumnType.INT)
        .execute());

        assertArrayEquals(emptyArray, QueryBuilder.createTable(storage)
            .table("customers")
            .column("a", ColumnType.varchar(11))
            .column("b", ColumnType.varchar(255))
            .column("c", ColumnType.varchar(1))
        .execute());

        assertArrayEquals(emptyArray, QueryBuilder.createTable(storage)
            .table("customers")
            .column("a", ColumnType.varchar(11), ConstraintMap.PRIMARY())
            .column("b", ColumnType.varchar(255), ConstraintMap.DEFAULT("a"))
            .column("c", ColumnType.varchar(1))
        .execute());
    }
}
