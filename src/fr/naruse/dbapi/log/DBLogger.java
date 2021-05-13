package fr.naruse.dbapi.log;

import com.google.common.collect.Lists;
import fr.naruse.dbapi.api.DatabaseAPI;
import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.sql.SQLHelper;
import fr.naruse.dbapi.sql.SQLRequest;

import java.sql.Timestamp;

public class DBLogger {
    private final DBAPICore core;
    private final boolean enable;
    private Database databaseLogs;
    private String tableName;

    public DBLogger(DBAPICore core) {
        this.core = core;
        this.enable = core.getPlugin().getConfigObject("logs.enable");

        if (!this.enable) {
            return;
        }

        this.tableName = core.getPlugin().getConfigObject("logs.tableName");
    }

    public void onEnable() {
        if (!this.enable) {
            return;
        }

        DatabaseAPI.createNewDatabase(this.databaseLogs = new Database("DBAPILogs", tableName, false) {
            @Override
            public String getQuery() {
                return "CREATE TABLE " + tableName + " (" + "time VARCHAR(255)," + "query TEXT," + "args VARCHAR(255)," + "inSecondThread VARCHAR(3)," + "identifier VARCHAR(255))";
            }
        });
    }

    public void log(SQLRequest sqlRequest, String identifier, boolean inSecondThread) {
        if (this.databaseLogs == null || !this.databaseLogs.isLoaded()) {
            core.getPlugin().scheduleTask(() -> log(sqlRequest, identifier, inSecondThread), 20);
            return;
        }

        log(sqlRequest.getSqlRequest(), identifier, inSecondThread, sqlRequest.getObjects());
    }

    private void log(String query, String identifier, boolean inSecondThread, Object... args) {
        if (!this.enable) {
            return;
        }

        String time = new Timestamp(System.currentTimeMillis()).toString();
        String arguments = Lists.newArrayList(args).toString().replace("[", "").replace("]", "");
        SQLRequest sqlRequest = new SQLRequest(SQLHelper.getInsertRequest(this.tableName, new String[]{"time", "query", "args", "inSecondThread", "identifier"}), time, query, arguments, inSecondThread ? "yes" : "no", identifier);

        this.databaseLogs.prepareStatement(sqlRequest);
    }
}
