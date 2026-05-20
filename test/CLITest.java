package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        cli.parseSql("");
    }
}
