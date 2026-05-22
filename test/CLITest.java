package test;

import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import core.CLI;
import exception.InvalidSyntaxException;

public class CLITest {
    private CLI cli;

    @Before
    public void init() {
        cli = new CLI();
    }

    @Test
    public void testInsert() throws InvalidSyntaxException {
        cli.parseSql("INSERT INTO customers (a, b, c) VALUES (1, 2, 3), (4, 5, 6)");
    }

    @Test()
    public void testSelect() throws InvalidSyntaxException {
        assertThrows(NullPointerException.class, () -> cli.parseSql("SELECT (a, b, c) FROM customers;"));
        assertThrows(NullPointerException.class, () -> cli.parseSql("SELECT * FROM customers;"));
        assertThrows(NullPointerException.class, () -> cli.parseSql("SELECT * FROM customers WHERE a = b;"));
        assertThrows(NullPointerException.class, () -> cli.parseSql("SELECT * FROM customers WHERE a = b AND true;"));
    }
}
