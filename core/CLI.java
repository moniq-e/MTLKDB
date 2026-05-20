package core;

import java.util.ArrayList;

import exception.InvalidSyntaxException;
import struct.Row;

public class CLI {
    private Storage storage;

    public void parseSql(String sql) throws InvalidSyntaxException {
        var args = sql.split(" ");

        switch (args[0]) {
            case "INSERT":
                if (!args[1].equals("INTO")) throw new InvalidSyntaxException("INSERT statements needs to be followed by INTO keyword.");

                var tableName = args[2];

                String[] columns = null;
                int end = 3, index;
                if (!args[3].equals("VALUES")) {
                    if (args[3].startsWith("(")) {
                        while (!args[end].endsWith(")")) end++;
                    } else {
                        throw new InvalidSyntaxException("Specify columns between parenthesis or do not and use VALUES for all columns.");
                    }
                    columns = extractColumns(args, 3, end);
                    index = end + 1;
                } else {
                    index = 3;
                }

                if (!args[index].equals("VALUES")) throw new InvalidSyntaxException("Use VALUES keyword before rows.");

                ArrayList<Row> rows = new ArrayList<>();
                while (!args[index].endsWith(";")) {
                    index++;
                    end = index;
                    if (args[end].startsWith("(")) {
                        while (!args[end].endsWith(")")) end++;
                    }
                    rows.add(extractRow(columns, args, index, end));
                    index = end;
                }

                if (rows.size() == 1) {
                    storage.insertRow(tableName, rows.getFirst());
                } else {
                    storage.insertRows(tableName, rows.toArray(new Row[rows.size()]));
                }
                break;

            default:
                break;
        }
    }

    private String[] extractColumns(String[] args, int init, int end) {
        var res = new String[end - init + 1];

        for (int i = 0; i < res.length; i++) {
            res[i] = args[init++].replaceAll("\\(|\\)|,", "");
        }
        return res;
    }

    private Row extractRow(String[] columns, String[] args, int init, int end) {
        var values = new Object[end - init + 1];

        for (int i = 0; i < values.length; i++) {
            values[i] = args[init++].replaceAll("\\(|\\)|,|;", "");
        }
        return new Row(columns, values);
    }
}
