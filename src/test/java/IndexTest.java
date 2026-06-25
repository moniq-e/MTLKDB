import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mtlk.mtlkdb.core.persistence.index.IndexLeafPage;
import com.mtlk.mtlkdb.dto.RecordIdsDTO;
import com.mtlk.mtlkdb.struct.RecordId;

public class IndexTest {
    private IndexLeafPage leaf;
    private IndexLeafPage leaf2;

    @BeforeEach
    public void init() {
        leaf = new IndexLeafPage(-1, -1, List.of(1, 2, 3, 4, 5, 6), sequentialRIDs(1, 6));
        leaf2 = new IndexLeafPage(-1, -1, List.of(1, 2, 3, 4, 5, 6, 7), sequentialRIDs(1, 7));
    }

    @Test
    public void testLeaf_getRecordIdByKey() {
        assertEquals(null, leaf.getRecordIdByKey(0));
        assertEquals(new RecordId(1, 10), leaf.getRecordIdByKey(1));
        assertEquals(new RecordId(3, 30), leaf.getRecordIdByKey(3));
        assertEquals(new RecordId(6, 60), leaf.getRecordIdByKey(6));

        assertEquals(null, leaf2.getRecordIdByKey(0));
        assertEquals(new RecordId(1, 10), leaf2.getRecordIdByKey(1));
        assertEquals(new RecordId(3, 30), leaf2.getRecordIdByKey(3));
        assertEquals(new RecordId(6, 60), leaf2.getRecordIdByKey(6));
        assertEquals(new RecordId(7, 70), leaf2.getRecordIdByKey(7));
    }

    @Test
    public void testLeaf_insertRecordId() {
        leaf.insert(7, new RecordId(7, 70));
        assertEquals(leaf2, leaf);
    }

    @Test
    public void testLeaf_getRecordIds() {
        assertEquals(recordIdsDTO(1, 6), leaf.getRecordIds(1, 6));
        assertEquals(recordIdsDTO(1, 5), leaf.getRecordIds(1, 5));
        assertEquals(recordIdsDTO(2, 5), leaf.getRecordIds(2, 5));
        assertEquals(recordIdsDTO(2, 6), leaf.getRecordIds(2, 6));
        assertEquals(recordIdsDTO(3, 3), leaf.getRecordIds(3, 3));
        assertEquals(recordIdsDTO(1, 6), leaf.getRecordIds(1, 6));

        assertEquals(recordIdsDTO(1, 5), leaf2.getRecordIds(1, 5));
        assertEquals(recordIdsDTO(2, 5), leaf2.getRecordIds(2, 5));
        assertEquals(recordIdsDTO(2, 6), leaf2.getRecordIds(2, 6));
        assertEquals(recordIdsDTO(3, 3), leaf2.getRecordIds(3, 3));
        assertEquals(recordIdsDTO(2, 3), leaf2.getRecordIds(2, 3));
    }

    @Test
    public void testLeaf_equals() {
        assertEquals(leaf, IndexLeafPage.deserialize(leaf.serialize()));
        assertNotEquals(leaf, leaf2);
    }

    @Test
    public void testLeaf_split() {
        var pk = leaf.getPromotionKey();
        var pk2 = leaf2.getPromotionKey();

        var splitted = leaf.split(0, 1);
        var splitted2 = leaf2.split(0, 1);

        assertEquals(leaf, new IndexLeafPage(-1, 1, List.of(1, 2, 3), sequentialRIDs(1, 3)));
        assertEquals(splitted, new IndexLeafPage(0, -1, List.of(4, 5, 6), sequentialRIDs(4, 6)));

        assertEquals(leaf2, new IndexLeafPage(-1, 1, List.of(1, 2, 3, 4), sequentialRIDs(1, 4)));
        assertEquals(splitted2, new IndexLeafPage(0, -1, List.of(5, 6, 7), sequentialRIDs(5, 7)));

        assertEquals(pk, splitted.getFirstKey());
        assertEquals(pk2, splitted2.getFirstKey());
    }

    public static RecordIdsDTO recordIdsDTO(int from, int to) {
        return new RecordIdsDTO(sequentialRIDs(from, to), to, false);
    }

    public static List<RecordId> sequentialRIDs(int from, int to) {
        var arr = new ArrayList<RecordId>();
        for (int i = from; i < to + 1; i++) {
            arr.add(new RecordId(i, i * 10));
        }
        return arr;
    }
}
