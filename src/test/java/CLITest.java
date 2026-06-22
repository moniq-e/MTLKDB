import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.mtlk.mtlkdb.core.Storage;
import com.mtlk.mtlkdb.core.query.QueryBuilder;
import com.mtlk.mtlkdb.exception.InvalidSyntaxException;
import com.mtlk.mtlkdb.expression.Equals;
import com.mtlk.mtlkdb.expression.Expression;
import com.mtlk.mtlkdb.expression.GreaterThan;
import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.ColumnType;
import com.mtlk.mtlkdb.struct.ConstraintMap;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.value.LiteralValue;

public class CLITest {
    private final RawRow[] emptyArray = new RawRow[0];

    private Storage storage;

    {
        try {
            storage = new Storage() {

                @Override
                public boolean createTable(String tableName, ColumnDefinition[] columns) {
                    return true;
                }

                @Override
                public boolean createDatabase(String dbName) {
                    return true;
                }

                @Override
                public int insertRow(String tableName, RawRow row) {
                    return 1;
                }

                @Override
                public int insertRows(String tableName, RawRow[] rows) {
                    return 1;
                }

                @Override
                public boolean deleteRow(String tableName, String primaryKey) {
                    return true;
                }

                @Override
                public int deleteRows(String tableName, String[] primaryKeys) {
                    return 1;
                }

                @Override
                public RawRow[] select(String tableName, String[] columns, Expression expression) {
                    return emptyArray;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInsert() throws InvalidSyntaxException {
        assertEquals(1, QueryBuilder.insert(storage)
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
            .and(new GreaterThan(new LiteralValue("1"), new LiteralValue("2")))
        .execute());
    }

    @Test
    public void testCreateTable() throws InvalidSyntaxException {
        assertEquals(true, QueryBuilder.createTable(storage)
            .table("customers")
            .column("a", ColumnType.INT)
            .column("b", ColumnType.INT)
            .column("c", ColumnType.INT)
        .execute());

        assertEquals(true, QueryBuilder.createTable(storage)
            .table("customers")
            .column("a", ColumnType.INT)
            .column("b", ColumnType.varchar(255), ConstraintMap.PRIMARY())
            .column("c", ColumnType.INT)
        .execute());

        assertEquals(true, QueryBuilder.createTable(storage)
            .table("customers")
            .column("a", ColumnType.varchar(11))
            .column("b", ColumnType.varchar(255))
            .column("c", ColumnType.varchar(1))
        .execute());

        assertEquals(true, QueryBuilder.createTable(storage)
            .table("customers")
            .column("a", ColumnType.varchar(11), ConstraintMap.PRIMARY())
            .column("b", ColumnType.varchar(255), ConstraintMap.DEFAULT("a"))
            .column("c", ColumnType.varchar(1))
        .execute());
    }
}
