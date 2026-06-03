package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;

import struct.ColumnDefinition;

public class Database {
    private Path dbDirectory;
    private HashMap<String, Table> tables;
    
    public Database(String dbName) throws IOException {
        dbDirectory = Files.createDirectory(Path.of("./", dbName), new FileAttribute[0]);

        tables = new HashMap<>();
    }

    public boolean createTable(String tableName, ColumnDefinition[] columns) {
        var table = new Table(tableName, columns);
        tables.put(tableName, table);
        return save(table);
    }

    private boolean save(Table table) {
        
    }
}
