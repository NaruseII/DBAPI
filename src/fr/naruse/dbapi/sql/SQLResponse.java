package fr.naruse.dbapi.sql;

import fr.naruse.dbapi.main.DBAPICore;

public abstract class SQLResponse {
    public void handleResponse(Object response) {

    }

    public void runSynchronously(DBAPICore core, Object o) {
        core.getPlugin().scheduleTask(() -> handleResponse(o), 0);
    }
}
