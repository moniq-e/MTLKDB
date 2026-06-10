package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import struct.util.Encoder;

public class GeneralTest {
    
    @Test
    public void testRegex() {
        assertEquals("1", "(1,);".replaceAll("\\(|\\)|,|;", ""));
    }

    @Test
    public void testIntEncode() {
        assertEquals(4, Encoder.decodeInt(Encoder.encodeInt(4)));
        assertEquals(0xFF, Encoder.decodeInt(Encoder.encodeInt(0xFF)));
        assertEquals(37898, Encoder.decodeInt(Encoder.encodeInt(37898)));
        assertEquals(-37898, Encoder.decodeInt(Encoder.encodeInt(-37898)));
    }
}
