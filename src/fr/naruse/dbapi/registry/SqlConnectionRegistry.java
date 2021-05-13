package fr.naruse.dbapi.registry;

import com.google.common.collect.Lists;
import fr.naruse.dbapi.sql.SQLConnection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SqlConnectionRegistry implements Iterable<SQLConnection> {
    private final ArrayList<SQLConnection> SQLConnections = Lists.newArrayList();

    public void add(SQLConnection sqlConnection) {
        this.SQLConnections.add(sqlConnection);
    }

    public void remove(SQLConnection sqlConnection) {
        this.SQLConnections.remove(sqlConnection);
    }

    public List<SQLConnection> getSQLConnections() {
        return this.SQLConnections;
    }

    @Override
    public Iterator<SQLConnection> iterator() {
        return this.SQLConnections.iterator();
    }
}
