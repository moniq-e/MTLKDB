package persistence.record;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mtlk.mtlkdb.core.Storage;
import com.mtlk.mtlkdb.core.query.QueryBuilder;
import com.mtlk.mtlkdb.expression.Equals;
import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.ColumnType;
import com.mtlk.mtlkdb.struct.ConstraintMap;
import com.mtlk.mtlkdb.struct.RawRow;
import com.mtlk.mtlkdb.struct.util.RawRowEncoder;
import com.mtlk.mtlkdb.struct.value.ColumnValue;
import com.mtlk.mtlkdb.struct.value.LiteralValue;

public class UsageTest {
    private Storage storage;

    @BeforeEach
    public void init() throws IOException {
        storage = new Storage();
        createTestSchema();
    }

    @AfterEach
    public void destroy() throws IOException {
        storage.closeTable("test");
        Files.delete(Path.of("./table_test/test-schema.json"));
        Files.delete(Path.of("./table_test/test.idx"));
        Files.delete(Path.of("./table_test/test.dat"));
        Files.delete(Path.of("./table_test"));
    }

    @Test
    public void testCreateTable() throws IOException {
        assertTrue(createTestTable());
    }

    @Test
    public void testInsert() {
        createTestTable();

        assertEquals(1, QueryBuilder.insert(storage)
            .into("test")
            .columns("a")
            .values("1")
            .execute());

        assertEquals(new RawRow(new String[] { "a" }, RawRowEncoder.encodeValues(new String[] { "1" })), QueryBuilder.select(storage)
            .from("test")
            .where(new Equals(new ColumnValue("a"), new LiteralValue("1")))
            .execute());
    }

    private void createTestSchema() throws IOException {
        var schema = new JSONArray();
        var columnA = new JSONObject();

        columnA.put("name", "a");
        columnA.put("type", "int");

        var constraints = new JSONArray();
        var pk = new JSONObject();

        pk.put("type", "primary");
        constraints.put(pk);
        columnA.put("constraints", constraints);

        schema.put(columnA);

        var schemaPath = Path.of("./table_test/test-schema.json");
        Files.createDirectories(schemaPath.getParent());
        Files.writeString(schemaPath, schema.toString());
    }

    private boolean createTestTable() {
        var columns = new ColumnDefinition[] {
            new ColumnDefinition("id", ColumnType.INT, new ConstraintMap[] { ConstraintMap.PRIMARY() })
        };
        return storage.createTable("test", columns);
    }
}
