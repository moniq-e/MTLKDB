package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import struct.util.RawEncoder;

public class GeneralTest {
    
    @Test
    public void testRegex() {
        assertEquals("1", "(1,);".replaceAll("\\(|\\)|,|;", ""));
    }

    @Test
    public void testIntEncode() {
        assertEquals(4, RawEncoder.decodeInt(RawEncoder.encodeInt(4)));
        assertEquals(0xFF, RawEncoder.decodeInt(RawEncoder.encodeInt(0xFF)));
        assertEquals(37898, RawEncoder.decodeInt(RawEncoder.encodeInt(37898)));
        assertEquals(-37898, RawEncoder.decodeInt(RawEncoder.encodeInt(-37898)));
    }
}
