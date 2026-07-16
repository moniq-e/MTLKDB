import static org.junit.jupiter.api.Assertions.*;

import static com.mtlk.mtlkdb.struct.encoder.Encoder.COMPARABLE;
import static com.mtlk.mtlkdb.struct.encoder.Encoder.PERSIST;

import org.junit.jupiter.api.Test;

public class GeneralTest {
    
    @Test
    public void testRegex() {
        assertEquals("1", "(1,);".replaceAll("\\(|\\)|,|;", ""));
    }

    @Test
    public void testComparableIntEncode() {
        assertEquals(4, COMPARABLE.decodeInt(COMPARABLE.encodeInt(4)));
        assertEquals(0xFF, COMPARABLE.decodeInt(COMPARABLE.encodeInt(0xFF)));
        assertEquals(37898, COMPARABLE.decodeInt(COMPARABLE.encodeInt(37898)));
        assertEquals(-37898, COMPARABLE.decodeInt(COMPARABLE.encodeInt(-37898)));
    }

    @Test
    public void testPersistIntEncode() {
        assertEquals(4, PERSIST.decodeInt(PERSIST.encodeInt(4)));
        assertEquals(0xFF, PERSIST.decodeInt(PERSIST.encodeInt(0xFF)));
        assertEquals(37898, PERSIST.decodeInt(PERSIST.encodeInt(37898)));
        assertEquals(-37898, PERSIST.decodeInt(PERSIST.encodeInt(-37898)));
    }
}
