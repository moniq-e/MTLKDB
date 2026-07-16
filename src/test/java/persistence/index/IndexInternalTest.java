package persistence.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mtlk.mtlkdb.core.persistence.index.IndexInternalPage;

public class IndexInternalTest {

    @Test
    public void testInternal_getChildPageId() {
        var page = buildSampleInternalPage();

        assertEquals(1, page.getChildPageId(0));
        assertEquals(2, page.getChildPageId(10));
        assertEquals(2, page.getChildPageId(15));
        assertEquals(3, page.getChildPageId(20));
        assertEquals(3, page.getChildPageId(25));
        assertEquals(4, page.getChildPageId(30));
        assertEquals(4, page.getChildPageId(35));
        assertEquals(5, page.getChildPageId(40));
        assertEquals(5, page.getChildPageId(100));
    }

    @Test
    public void testInternal_insertAndRemoveChildPageId() {
        var page = new IndexInternalPage(1);

        page.insertChildPageId(20, 3);
        page.insertChildPageId(10, 2);
        page.insertChildPageId(30, 4);

        assertEquals(1, page.getChildPageId(-1));
        assertEquals(2, page.getChildPageId(10));
        assertEquals(2, page.getChildPageId(15));
        assertEquals(3, page.getChildPageId(20));
        assertEquals(3, page.getChildPageId(29));
        assertEquals(4, page.getChildPageId(30));

        assertTrue(page.removeChildPageId(20));
        assertFalse(page.removeChildPageId(999));

        assertEquals(2, page.getChildPageId(15));
        assertEquals(2, page.getChildPageId(25));
        assertEquals(4, page.getChildPageId(30));
        assertEquals(4, page.getChildPageId(100));
    }

    @Test
    public void testInternal_serializeDeserialize() {
        var page = buildSampleInternalPage();
        var serialized = page.serialize();

        var deserialized = IndexInternalPage.deserialize(serialized);
        assertNotNull(deserialized);
        assertEquals(serialized, deserialized.serialize());

        assertEquals(page.getChildPageId(0), deserialized.getChildPageId(0));
        assertEquals(page.getChildPageId(20), deserialized.getChildPageId(20));
        assertEquals(page.getChildPageId(40), deserialized.getChildPageId(40));
    }

    @Test
    public void testInternal_split() {
        var page = buildSampleInternalPage();
        var secondPage = (IndexInternalPage) page.split(0, 100);

        assertNotNull(secondPage);

        // Original page should keep the left half (keys 10 and 20)
        assertEquals(1, page.getChildPageId(0));
        assertEquals(2, page.getChildPageId(10));
        assertEquals(3, page.getChildPageId(20));
        assertEquals(3, page.getChildPageId(25));
        assertEquals(3, page.getChildPageId(40));

        // Split page should contain the right half (key 40) and proper child pointers
        assertEquals(4, secondPage.getChildPageId(0));
        assertEquals(4, secondPage.getChildPageId(39));
        assertEquals(5, secondPage.getChildPageId(40));
        assertEquals(5, secondPage.getChildPageId(100));
    }

    private IndexInternalPage buildSampleInternalPage() {
        var page = new IndexInternalPage(1);
        page.insertChildPageId(10, 2);
        page.insertChildPageId(20, 3);
        page.insertChildPageId(30, 4);
        page.insertChildPageId(40, 5);
        return page;
    }
}
