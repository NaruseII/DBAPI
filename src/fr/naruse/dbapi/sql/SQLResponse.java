package fr.naruse.dbapi.sql;

import fr.naruse.dbapi.main.DBAPICore;

public abstract class SQLResponse<T> {
    public void handleResponse(T response) {

    }

    public void runSynchronously(DBAPICore core, T o) {
        core.getPlugin().scheduleTask(() -> handleResponse(o), 0);
    }
}
