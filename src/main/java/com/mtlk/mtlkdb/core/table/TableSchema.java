package com.mtlk.mtlkdb.core.table;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import com.mtlk.mtlkdb.struct.ColumnDefinition;
import com.mtlk.mtlkdb.struct.ColumnType;
import com.mtlk.mtlkdb.struct.ConstraintMap;

/**
 * name: String,<br>
 * type: ColumnType,<br>
 * size?: int,<br>
 * constraints?: ConstraintMap[] { type: ConstraintType, value?: Object }
 */
public class TableSchema {
    private File columnsFile;
    private JSONArray columnsJson;
    private HashMap<String, ColumnDefinition> columns;
    private String[] columnNames;

    public TableSchema(File file) throws JSONException, IOException {
        columnsFile = file;
        init(columnsFile);
    }

    private void init(File file) throws JSONException, IOException {
        try (var fr = new FileReader(file)) {    
            columnsJson = new JSONArray(fr.readAllAsString());
        }

        columnNames = new String[columnsJson.length()];
        columns = new HashMap<>(columnsJson.length());
        for (int i = 0; i < columns.size(); i++) {
            var jsonColumn = columnsJson.getJSONObject(i);

            var name = jsonColumn.getString("name");
            var type = ColumnType.fromString(jsonColumn.getString("type"), jsonColumn.optInt("size"));

            ConstraintMap[] constraints = null;

            var jsonConstraints = jsonColumn.optJSONArray("constraints");
            if (jsonConstraints != null) {
                constraints = new ConstraintMap[jsonConstraints.length()];

                for (int j = 0; j < constraints.length; j++) {
                    var jsonConstraint = jsonConstraints.getJSONObject(i);
                    constraints[j] = ConstraintMap.from(jsonConstraint.getString("type"), jsonConstraint.opt("value"));
                }
            }

            columnNames[i] = name;
            columns.put(name, new ColumnDefinition(jsonColumn.getString("name"), type, constraints));
        }
    }

    public ColumnDefinition get(int pos) {
        return get(columnNames[pos]);
    }

    public ColumnDefinition get(String columnName) {
        return columns.get(columnName);
    }

    public void save() throws IOException {
        try (var fw = new FileWriter(columnsFile, false)) {
            fw.write(columnsJson.toString()); 
        }
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public int size() {
        return columns.size();
    }
}
