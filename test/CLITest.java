package test;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import core.query.QueryBuilder;
import exception.InvalidSyntaxException;
import expression.Equals;
import struct.ColumnType;
import struct.ConstraintMap;
import struct.value.LiteralValue;

public class CLITest {

    @Test
    public void testInsert() throws InvalidSyntaxException {
        assertThrows(NullPointerException.class, () -> QueryBuilder.insert(null)
            .into("customers")
            .values("a", "b", "c")
            .values("d", "e", "f")
        .execute());
    }

    @Test()
    public void testSelect() throws InvalidSyntaxException {
        assertThrows(NullPointerException.class, () -> QueryBuilder.select(null)
            .columns("a", "b", "c")
            .from("customers")
        .execute());

        assertThrows(NullPointerException.class, () -> QueryBuilder.select(null)
            .columns("*")
            .from("customers")
        .execute());

        assertThrows(NullPointerException.class, () -> QueryBuilder.select(null)
            .columns("*")
            .from("customers")
            .where(new Equals(new LiteralValue("a"), new LiteralValue("b")))
        .execute());

        assertThrows(NullPointerException.class, () -> QueryBuilder.select(null)
            .columns("*")
            .from("customers")
            .where(new Equals(new LiteralValue("a"), new LiteralValue("b")))
            .and(row -> true)
        .execute());
    }

    @Test
    public void testCreateTable() throws InvalidSyntaxException {
        assertThrows(NullPointerException.class, () -> QueryBuilder.createTable(null)
            .table("customers")
            .column("a", ColumnType.INT)
            .column("b", ColumnType.INT)
            .column("c", ColumnType.INT)
        .execute());

        assertThrows(NullPointerException.class, () -> QueryBuilder.createTable(null)
            .table("customers")
            .column("a", ColumnType.INT)
            .column("b", ColumnType.varchar(255), ConstraintMap.PRIMARY())
            .column("c", ColumnType.INT)
        .execute());

        assertThrows(NullPointerException.class, () -> QueryBuilder.createTable(null)
            .table("customers")
            .column("a", ColumnType.varchar(11))
            .column("b", ColumnType.varchar(255))
            .column("c", ColumnType.varchar(1))
        .execute());

        assertThrows(NullPointerException.class, () -> QueryBuilder.createTable(null)
            .table("customers")
            .column("a", ColumnType.varchar(11), ConstraintMap.PRIMARY())
            .column("b", ColumnType.varchar(255), ConstraintMap.DEFAULT("a"))
            .column("c", ColumnType.varchar(1))
        .execute());
    }
}
