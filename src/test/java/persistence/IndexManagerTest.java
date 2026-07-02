package persistence;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mtlk.mtlkdb.core.persistence.index.IndexManager;
import com.mtlk.mtlkdb.struct.RecordId;

public class IndexManagerTest {
    private Path indexPath;
    private IndexManager indexManager;

    @BeforeEach
    public void setUp() throws IOException {
        var fileName = "index-" + UUID.randomUUID() + ".idx";
        indexPath = Path.of(fileName);
        indexManager = new IndexManager(fileName);
    }

    @AfterEach
    public void tearDown() throws IOException {
        indexManager.close();
        if (indexPath != null) {
            Files.deleteIfExists(indexPath);
        }
    }

    @Test
    public void testInsertAndSearchByKey() throws IOException {
        insertKeys(5);

        assertEquals(new RecordId(1, 10), indexManager.search(1));
        assertEquals(new RecordId(2, 20), indexManager.search(2));
        assertEquals(new RecordId(3, 30), indexManager.search(3));
        assertEquals(new RecordId(5, 50), indexManager.search(5));
        assertNull(indexManager.search(99));
    }

    @Test
    public void testRangeSearchAcrossPages() throws IOException {
        insertKeys(350);

        var expected = new RecordId[5];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = new RecordId(101 + i, (101 + i) * 10);
        }

        assertArrayEquals(expected, indexManager.search(101, 105));

        expected = new RecordId[250];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = new RecordId(101 + i, (101 + i) * 10);
        }

        assertArrayEquals(expected, indexManager.search(101, 350));
        assertArrayEquals(expected, indexManager.search(101, 400));
    }

    @Test
    public void testRemoveKeyUpdatesSearchResults() throws IOException {
        insertKeys(10);

        indexManager.remove(5);

        assertNull(indexManager.search(5));
        assertArrayEquals(
            new RecordId[] {
                new RecordId(4, 40),
                new RecordId(6, 60)
            },
            indexManager.search(4, 6)
        );
    }

    private void insertKeys(int count) throws IOException {
        for (int key = 1; key <= count; key++) {
            indexManager.insert(key, new RecordId(key, key * 10));
        }
    }
}
