package fr.naruse.dbapi.util;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.sql.SQLConnection;

public class SqlConnectionRunnable {
    private final DBAPICore core;

    public SqlConnectionRunnable(DBAPICore core) {
        this.core = core;
        run();
    }

    public void run() {
        core.getPlugin().scheduleTask(() -> {
            for (SQLConnection sqlConnection : this.core.getSqlConnectionRegistry()) {
                sqlConnection.connection(true);
            }
            run();
        }, 20 * 60 * 60 * 4L);
    }
}
