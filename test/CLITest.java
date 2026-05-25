package test;

import static org.junit.Assert.assertThrows;

import org.junit.Before;
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
       assertThrows(NullPointerException.class, () -> cli.parseSql("INSERT INTO customers (a, b, c) VALUES (1, 2, 3), (4, 5, 6)"));
    }

    @Test()
    public void testSelect() throws InvalidSyntaxException {
        assertThrows(NullPointerException.class, () -> cli.parseSql("SELECT (a, b, c) FROM customers;"));
        assertThrows(NullPointerException.class, () -> cli.parseSql("SELECT * FROM customers;"));
        assertThrows(NullPointerException.class, () -> cli.parseSql("SELECT * FROM customers WHERE a = b;"));
        assertThrows(NullPointerException.class, () -> cli.parseSql("SELECT * FROM customers WHERE a = b AND true;"));
    }

    @Test
    public void testCreateTable() throws InvalidSyntaxException {
       //assertThrows(NullPointerException.class, () -> cli.parseSql("CREATE TABLE customers (a int, b int, c int)"));
       //assertThrows(NullPointerException.class, () -> cli.parseSql("CREATE TABLE customers (a int PRIMARY, b varchar(255) PRIMARY, c int)"));
       //assertThrows(NullPointerException.class, () -> cli.parseSql("CREATE TABLE customers (a varchar(11), b varchar(255), c varchar(1));"));
       assertThrows(NullPointerException.class, () -> cli.parseSql("CREATE TABLE customers (a varchar(11) PRIMARY, b varchar(255) DEFAULT a, c varchar(1));"));
    }
}
