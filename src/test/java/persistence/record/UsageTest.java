package persistence.record;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mtlk.mtlkdb.core.Storage;
import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.ColumnType;
import com.mtlk.mtlkdb.struct.ConstraintMap;

public class UsageTest {
    private Storage storage;

    @BeforeEach
    public void init() throws IOException {
        storage = new Storage();
    }

    @Test
    public void testCreateTable() throws IOException {
        createTestSchema();
        assertTrue(createTestTable());
    }

    @Test
    public void testInsert() {

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

        Files.writeString(Path.of("./table_test/test.schema"), schema.toString());
    }

    private boolean createTestTable() {
        var columns = new ColumnDefinition[] {
            new ColumnDefinition("id", ColumnType.INT, new ConstraintMap[] { ConstraintMap.PRIMARY() })
        };
        return storage.createTable("test", columns);
    }
}
