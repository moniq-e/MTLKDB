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
import struct.RawRow;
import struct.value.LiteralValue;

public class CLITest {
    private Storage storage = new Storage() {
        @Override
        public RawRow[] createTable(String tableName, ColumnDefinition[] columns) {
            return new RawRow[0];
        }

        @Override
        public RawRow[] createDatabase(String dbName) {
            return new RawRow[0];
        }

        @Override
        public RawRow[] insertRow(String tableName, RawRow row) {
            return new RawRow[] { row };
        }

        @Override
        public RawRow[] insertRows(String tableName, RawRow[] rows) {
            return new RawRow[0];
        }

        @Override
        public RawRow[] deleteRow(String tableName, Object primaryKey) {
            return new RawRow[0];
        }

        @Override
        public RawRow[] deleteRows(String tableName, Object[] primaryKeys) {
            return new RawRow[0];
        }

        @Override
        public RawRow[] select(String tableName, String[] columns, expression.Expression expression) {
            return new RawRow[0];
        }
    };
    private RawRow[] emptyArray = new RawRow[0];

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
