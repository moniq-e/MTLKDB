package core;

import java.util.ArrayList;
import java.util.Collections;

import exception.InvalidSyntaxException;
import expression.Expression;
import struct.Row;

public class CLI {
    private Storage storage;

    public Row[] parseSql(String sql) throws InvalidSyntaxException {
        var tokens = new ArrayList<String>();

        sql = sql.trim();

        if (!sql.endsWith(";")) sql.concat(";");

        sql = sql
            .replace(";", " ; ")
            .replace("(", " ( ")
            .replace(")", " ) ")
            .replace("=", " = ")
            .replace("!=", " != ")
            .replace(">", " > ")
        .replace("<", " < ");

        Collections.addAll(tokens, sql.split("\\s+"));

        switch (tokens.get(0).toUpperCase()) {
            case "INSERT":
                if (!tokens.get(1).toUpperCase().equals("INTO")) throw new InvalidSyntaxException("INSERT statements needs to be followed by INTO keyword.");

                var tableName = tokens.get(2);

                String[] columns = null;
                int end = 4, index;
                if (!tokens.get(3).toUpperCase().equals("VALUES")) {
                    if (tokens.get(3).equals("(")) {
                        while (!tokens.get(end).equals(")")) end++;
                    } else {
                        throw new InvalidSyntaxException("Specify columns between parenthesis or do not and use VALUES for all columns.");
                    }
                    //end == )
                    columns = extractColumns(tokens, 4, end - 1);
                    index = end + 1;
                } else {
                    index = 3;
                }

                if (!tokens.get(index).toUpperCase().equals("VALUES")) throw new InvalidSyntaxException("Use VALUES keyword before rows.");

                //index == VALUES
                var rows = new ArrayList<Row>();
                while (!tokens.get(index).equals(";")) {
                    index++;
                    end = index;
                    if (tokens.get(end).equals("(")) {
                        while (!tokens.get(end).equals(")")) end++;
                    }
                    //end == )
                    rows.add(extractRow(columns, tokens, index + 1, end - 1));
                    index = end + 1;
                }

                if (rows.size() == 1) {
                    return storage.insertRow(tableName, rows.getFirst());
                } else {
                    return storage.insertRows(tableName, rows.toArray(new Row[rows.size()]));
                }

            case "SELECT":
                columns = null;
                end = 2;
                if (!tokens.get(1).equals("*")) {
                    if (tokens.get(1).equals("(")) {
                        while (!tokens.get(end).equals(")")) end++;
                    } else {
                        throw new InvalidSyntaxException("Specify columns between parenthesis or use '*' for all columns.");
                    }
                    //end == )
                    columns = extractColumns(tokens, 2, end - 1);
                    index = end + 1;
                } else {
                    index = 2;
                }

                if (!tokens.get(index).toUpperCase().equals("FROM")) throw new InvalidSyntaxException("Use 'FROM table_name' to select from a table.");
                tableName = tokens.get(++index);

                Expression expression = null;
                if (!tokens.get(++index).equals(";")) {
                    switch (tokens.get(index).toUpperCase()) {
                        case "WHERE":
                            expression = WhereParser.parseWhere(tokens, index + 1);
                            break;
                    }
                }

                return storage.select(tableName, columns, expression);
        }
        return null;
    }

    private String[] extractColumns(ArrayList<String> tokens, int init, int end) {
        var res = new String[end - init + 1];

        for (int i = 0; i < res.length; i++) {
            res[i] = tokens.get(init++).replace(",", "");
        }
        return res;
    }

    private Row extractRow(String[] columns, ArrayList<String> tokens, int init, int end) {
        var values = new Object[end - init + 1];

        for (int i = 0; i < values.length; i++) {
            values[i] = tokens.get(init++).replace(",", "");
        }
        return new Row(columns, values);
    }
}
