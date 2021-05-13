package fr.naruse.dbapi.api;

import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.log.DBLogger;
import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.sql.SQLConnection;

import java.util.Iterator;
import java.util.Set;

public class DatabaseAPI {
    private static DBAPICore core;

    public static void setDBAPICore(DBAPICore dbapiCore) {
        core = dbapiCore;
    }

    public static void createNewDatabase(Database database) {
        core.getDatabaseRegistry().add(core, database);
        core.getAvailableSqlConnection().initDatabase(database);
        database.setPlugin(core);
    }

    public static Database getDatabase(String identifier) {
        return core.getDatabaseRegistry().getDatabase(identifier);
    }

    public static Iterator<Database> getDatabases() {
        return core.getDatabaseRegistry().iterator();
    }

    public static Set<String> getIdentifiers() {
        return core.getDatabaseRegistry().getIdentifiers();
    }

    public static Iterator<SQLConnection> getSqlConnections() {
        return core.getSqlConnectionRegistry().iterator();
    }

    public static DBLogger getLogger() {
        return core.getDBLogger();
    }

    public static void openNewConnection() {
        core.openNewConnection();
    }

    public static boolean isSecuritySystemActivated() {
        return core.getSecurityManager().isActivated();
    }
}
