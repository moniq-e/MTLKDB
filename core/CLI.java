package core;

import java.util.ArrayList;
import java.util.Collections;

import dto.ExtConstDTO;
import exception.InvalidSyntaxException;
import expression.Expression;
import struct.Row;
import struct.Column;
import struct.ColumnType;
import struct.ConstraintMap;
import struct.Constraints;

public class CLI {
    private Storage storage;

    public Row[] parseSql(String sql) throws InvalidSyntaxException {
        var tokens = new ArrayList<String>();

        sql = sql.trim();

        if (!sql.endsWith(";")) sql = sql.concat(";");

        sql = sql
            .replace(",)", ")")
            .replace(",", " , ")
            .replace(";", " ; ")
            .replace("(", " ( ")
            .replace(")", " ) ")
            .replace("!=", " != ")
            .replaceAll("  (?<!!)=", " = ")
            .replace(">", " > ")
        .replace("<", " < ");

        Collections.addAll(tokens, sql.split("\\s+"));

        switch (tokens.get(0).toUpperCase()) {
            case "INSERT":
                return parseInsert(tokens);
            case "SELECT":
                return parseSelect(tokens);
            case "CREATE":
                return parseCreate(tokens);
        }
        return null;
    }

    private Row[] parseInsert(ArrayList<String> tokens) throws InvalidSyntaxException {
        if (!tokens.get(1).equalsIgnoreCase("INTO")) throw new InvalidSyntaxException("INSERT statements needs to be followed by INTO keyword.");

        var tableName = tokens.get(2);

        String[] columns = null;
        int end = 4, index;
        if (!tokens.get(3).equalsIgnoreCase("VALUES")) {
            if (tokens.get(3).equals("(")) {
                while (!tokens.get(end).equals(")")) end++;
            } else {
                throw new InvalidSyntaxException("Specify columns between parenthesis or do not and use VALUES for all columns.");
            }
            //end == )
            columns = extractColumnNames(tokens, 4, end);
            index = end + 1;
        } else {
            index = 3;
        }

        if (!tokens.get(index).equalsIgnoreCase("VALUES")) throw new InvalidSyntaxException("Use VALUES keyword before rows.");

        //index == VALUES
        var rows = new ArrayList<Row>();
        while (!tokens.get(index).equals(";")) {
            index++;
            end = index;
            if (tokens.get(end).equals("(")) {
                while (!tokens.get(end).equals(")")) end++;
            }
            //end == )
            rows.add(extractRow(columns, tokens, index + 1, end));
            index = end + 1;
        }

        if (rows.size() == 1) {
            return storage.insertRow(tableName, rows.getFirst());
        } else {
            return storage.insertRows(tableName, rows.toArray(new Row[rows.size()]));
        }
    }

    private Row[] parseSelect(ArrayList<String> tokens) throws InvalidSyntaxException {
        String[] columns = null;
        int end = 2, index;
        if (!tokens.get(1).equals("*")) {
            if (tokens.get(1).equals("(")) {
                while (!tokens.get(end).equals(")")) end++;
            } else {
                throw new InvalidSyntaxException("Specify columns between parenthesis or use '*' for all columns.");
            }
            //end == )
            columns = extractColumnNames(tokens, 2, end);
            index = end + 1;
        } else {
            index = 2;
        }

        if (!tokens.get(index).equalsIgnoreCase("FROM")) throw new InvalidSyntaxException("Use 'FROM table_name' to select from a table.");
        var tableName = tokens.get(++index);

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

    private Row[] parseCreate(ArrayList<String> tokens) throws InvalidSyntaxException {
        var error = new InvalidSyntaxException("Missing table or database name.");
        if (tokens.size() < 3) throw error;

        var name = tokens.get(2);

        switch (tokens.get(1).toUpperCase()) {
            case "TABLE":
                var columns = extractColumns(tokens, 4);
                return storage.createTable(name, columns);
            case "DATABASE":
                
                return storage.createDatabase(name);
            default:
                throw new InvalidSyntaxException("INSERT statements needs to be followed by INTO keyword.");
        }
    }

    private Column[] extractColumns(ArrayList<String> tokens, int init) throws InvalidSyntaxException {
        var res = new ArrayList<Column>();

        while (!tokens.get(init).equals(";")) {
            var name = tokens.get(init++);
            var type = ColumnType.fromString(tokens.get(init), tokens.get(init + 2));
            
            if (type.isVarchar()) {
                init += 4;
            } else init++;

            ConstraintMap[] constraints = null;
            if (tokens.get(init).equals(",") || tokens.get(init).equals(")")) {
                init++;
            } else {
                var dto = extractConstraints(tokens, init);
                constraints = dto.constraints();
                init += dto.diff();
            }

            res.add(new Column(name, type, constraints));
        }
        return res.toArray(new Column[res.size()]);
    }

    private ExtConstDTO extractConstraints(ArrayList<String> tokens, int init) throws InvalidSyntaxException {
        var constraints = new ArrayList<ConstraintMap>();
        var index = init;

        while (index < tokens.size()) {
            var token = tokens.get(index);
            if (token.equals(",") || token.equals(")") || token.equals(";")) {
                index++;
                break;
            }

            switch (token.toUpperCase()) {
                case "PRIMARY" -> {
                    index++;
                    constraints.add(new ConstraintMap(Constraints.PRIMARY, null));
                }
                case "DEFAULT" -> {
                    index++;
                    if (index >= tokens.size()) break;

                    var valueToken = tokens.get(index).replaceAll("^'+|'+$", "");
                    var value = valueToken.equalsIgnoreCase("NULL") ? null : valueToken;
                    constraints.add(new ConstraintMap(Constraints.DEFAULT, value));
                    index++;
                }
                default -> {
                    throw new InvalidSyntaxException("Invalid keyword '" + token + "'.");
                }
            }
        }
        return new ExtConstDTO(constraints.toArray(new ConstraintMap[constraints.size()]), index - init);
    }

    /**
     * @param init Inclusive
     * @param end Exclusive
     */
    private String[] extractColumnNames(ArrayList<String> tokens, int init, int end) {
        var res = new ArrayList<String>();

        for (int i = init; i < end; i++) {
            var name = tokens.get(i++);
            if (!name.equals(",")) res.add(name);
        }
        return res.toArray(new String[res.size()]);
    }

    /**
     * @param init Inclusive
     * @param end Exclusive
     */
    private Row extractRow(String[] columns, ArrayList<String> tokens, int init, int end) {
        var values = new ArrayList<Object>();

        for (int i = init; i < end; i++) {
            var name = tokens.get(i++);
            if (!name.equals(",")) values.add(name);
        }
        return new Row(columns, values.toArray());
    }
}