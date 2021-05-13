package fr.naruse.dbapi.main;

import fr.naruse.dbapi.api.DatabaseAPI;
import fr.naruse.dbapi.log.DBLogger;
import fr.naruse.dbapi.registry.DatabaseRegistry;
import fr.naruse.dbapi.registry.SqlConnectionRegistry;
import fr.naruse.dbapi.security.SecurityManager;
import fr.naruse.dbapi.sql.SQLConnection;
import fr.naruse.dbapi.util.Message;
import fr.naruse.dbapi.util.SqlConnectionRunnable;

public class DBAPICore {
    private final DatabaseRegistry DATABASE_REGISTRY = new DatabaseRegistry();
    private final SqlConnectionRegistry SQL_CONNECTION_REGISTRY = new SqlConnectionRegistry();
    private final boolean isOnBukkit;
    private DBLogger dBLogger;
    private SecurityManager securityManager;
    private int availableConnections;
    private int requestSeparator = 50;
    private IDBAPIPlugin plugin;

    public DBAPICore(boolean isBukkit) {
        this.isOnBukkit = isBukkit;
    }

    public void onLoad(IDBAPIPlugin plugin) {
        this.plugin = plugin;
        plugin.sendConsoleMessage(Message.B.getMessage() + " §aLoading DatabaseAPI...");

        this.securityManager = new SecurityManager(this);

        DatabaseAPI.setDBAPICore(this);

        for (int i = 0; i < this.availableConnections; i++) {
            SQLConnection sqlConnection = new SQLConnection(this, "jdbc:mysql://", plugin.getConfigObject("host"), plugin.getConfigObject("database"), plugin.getConfigObject("user"), plugin.getConfigObject("pass"), i + 1);
            sqlConnection.connection(false);
            SQL_CONNECTION_REGISTRY.add(sqlConnection);
        }

        this.dBLogger = new DBLogger(this);
    }

    public void onEnable() {
        this.dBLogger.onEnable();

        new SqlConnectionRunnable(this);
    }


    public void onDisable() {

    }

    public void openNewConnection() {
        SQLConnection sqlConnection = new SQLConnection(this, "jdbc:mysql://", plugin.getConfigObject("host"), plugin.getConfigObject("database"), plugin.getConfigObject("user"), plugin.getConfigObject("pass"), SQL_CONNECTION_REGISTRY.getSQLConnections().size() + 1);
        sqlConnection.connection(false);
        SQL_CONNECTION_REGISTRY.add(sqlConnection);
    }

    public SQLConnection getAvailableSqlConnection() {
        SQLConnection freeConnection = null;
        SQLConnection lessUsedConnection = null;

        int queueSize = 100000;

        for (SQLConnection sqlConnection : SQL_CONNECTION_REGISTRY) {

            if (sqlConnection.getSqlThread().getQueueSize() < queueSize) {
                queueSize = sqlConnection.getSqlThread().getQueueSize();
                lessUsedConnection = sqlConnection;
            }

            if (!sqlConnection.getSqlThread().isRunning()) {
                freeConnection = sqlConnection;
                break;
            }
        }
        if (freeConnection != null) {
            return freeConnection;
        }
        if (lessUsedConnection == null) {
            return SQL_CONNECTION_REGISTRY.getSQLConnections().get(0);
        }
        return lessUsedConnection;
    }

    public void setAvailableConnections(int availableConnections) {
        this.availableConnections = availableConnections;
    }

    public DatabaseRegistry getDatabaseRegistry() {
        return DATABASE_REGISTRY;
    }

    public SqlConnectionRegistry getSqlConnectionRegistry() {
        return SQL_CONNECTION_REGISTRY;
    }

    public DBLogger getDBLogger() {
        return this.dBLogger;
    }

    public int getRequestSeparator() {
        return requestSeparator;
    }

    public void setRequestSeparator(int requestSeparator) {
        this.requestSeparator = requestSeparator;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public IDBAPIPlugin getPlugin() {
        return plugin;
    }

    public boolean isOnBukkit() {
        return isOnBukkit;
    }
}
