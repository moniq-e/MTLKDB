package com.mtlk.mtlkdb.core.query;

import com.mtlk.mtlkdb.struct.RawRow;

public interface Query {

    Query select(String... columns);

    Query from(String table);

    Query where(String column, String op, Object value);

    Query and(String column, String op, Object value);

    Query or(String column, String op, Object value);

    RawRow[] execute();
}
