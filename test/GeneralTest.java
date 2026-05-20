package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class GeneralTest {
    
    @Test
    public void testRegex() {
        assertEquals("1", "(1,);".replaceAll("\\(|\\)|,|;", ""));
    }
}
