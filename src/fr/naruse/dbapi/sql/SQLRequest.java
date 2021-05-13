package fr.naruse.dbapi.sql;

import com.google.common.collect.Lists;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLRequest {
    private final String sqlRequest;
    private final List<Object> objects;

    public SQLRequest(String sqlRequest, Object... parameters) {
        this.sqlRequest = sqlRequest;
        this.objects = Lists.newArrayList(parameters);
    }

    public String getSqlRequest() {
        return this.sqlRequest;
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        for (int i = 0; i < this.objects.size(); i++) {
            ps.setObject(i + 1, this.objects.get(i));
        }
    }

    public List<Object> getObjects() {
        return this.objects;
    }

    public static class GetObject extends SQLRequest {
        private final String columnName;

        public GetObject(String sqlRequest, String columnName, Object... objects) {
            super(sqlRequest, objects);
            this.columnName = columnName;
        }

        public String getColumnName() {
            return this.columnName;
        }
    }
}
