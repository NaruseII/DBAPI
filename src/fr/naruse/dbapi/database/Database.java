package fr.naruse.dbapi.database;

import com.google.common.collect.Maps;
import fr.naruse.dbapi.api.event.SQLRequestEvent;
import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.sql.SQLRequest;
import fr.naruse.dbapi.sql.SQLResponse;

import java.sql.ResultSet;
import java.util.Map;

public abstract class Database {
    private final String identifier;
    private final String tableName;
    private final boolean enableLogs;
    private DBAPICore core;
    private boolean isLoaded = false;
    private long callCount = 0;
    private final Map<String, Integer> codeCallCount = Maps.newHashMap();

    public Database(String identifier, String tableName) {
        this(identifier, tableName, true);
    }

    public Database(String identifier, String tableName, boolean enableLogs) {
        this.identifier = identifier;
        this.tableName = tableName;
        this.enableLogs = enableLogs;
    }

    public abstract String getQuery();

    public void prepareStatement(SQLRequest sqlRequest) {
        SQLRequest finalSQLRequest = callEvent(true, SQLRequestEvent.RequestType.PREPARE_STATEMENT, sqlRequest);

        if (finalSQLRequest == null) {
            return;
        }

        log(sqlRequest, true);
        this.core.getAvailableSqlConnection().prepareStatement(finalSQLRequest, null, this);
    }

    public void prepareStatement(SQLRequest sqlRequest, SQLResponse response) {
        SQLRequest finalSQLRequest = callEvent(true, SQLRequestEvent.RequestType.PREPARE_STATEMENT, sqlRequest);

        if (finalSQLRequest == null) {
            return;
        }

        log(sqlRequest, true);
        this.core.getAvailableSqlConnection().prepareStatement(finalSQLRequest, response, this);
    }

    public SQLResponse getObject(SQLRequest.GetObject getObject, SQLResponse asyncResponse) {
        SQLRequest.GetObject finalSqlRequest = (SQLRequest.GetObject) callEvent(true, SQLRequestEvent.RequestType.GET_OBJECT, getObject);

        if (finalSqlRequest == null) {
            asyncResponse.handleResponse(null);
            return asyncResponse;
        }

        log(getObject, true);
        return this.core.getAvailableSqlConnection().getObject(finalSqlRequest, asyncResponse, this);
    }

    public SQLResponse hasAccount(SQLRequest sqlRequest, SQLResponse asyncResponse) {
        SQLRequest finalSQLRequest = callEvent(true, SQLRequestEvent.RequestType.EXISTS, sqlRequest);
        if (finalSQLRequest == null) {
            asyncResponse.handleResponse(null);
            return asyncResponse;
        }
        log(sqlRequest, true);
        return core.getAvailableSqlConnection().hasAccount(finalSQLRequest, asyncResponse, this);
    }

    public SQLResponse getResultSet(SQLRequest sqlRequest, SQLResponse asyncResponse) {
        SQLRequest finalSQLRequest = callEvent(true, SQLRequestEvent.RequestType.RESULT_SET, sqlRequest);

        if (finalSQLRequest == null) {
            asyncResponse.handleResponse(null);
            return asyncResponse;
        }

        log(sqlRequest, true);
        return core.getAvailableSqlConnection().getResultSet(finalSQLRequest, asyncResponse, this);
    }

    @Deprecated
    public Object getDirectObject(SQLRequest.GetObject getObject) {
        SQLRequest.GetObject finalSqlRequest = (SQLRequest.GetObject) callEvent(false, SQLRequestEvent.RequestType.GET_OBJECT, getObject);

        if (finalSqlRequest == null) {
            return null;
        }

        log(getObject, false);
        return this.core.getAvailableSqlConnection().getObject(finalSqlRequest);
    }

    @Deprecated
    public boolean hasDirectAccount(SQLRequest sqlRequest) {
        SQLRequest finalSQLRequest = callEvent(false, SQLRequestEvent.RequestType.EXISTS, sqlRequest);
        log(sqlRequest, false);
        return this.core.getAvailableSqlConnection().hasAccount(finalSQLRequest);
    }

    @Deprecated
    public void prepareDirectStatement(SQLRequest sqlRequest) {
        SQLRequest finalSQLRequest = callEvent(false, SQLRequestEvent.RequestType.PREPARE_STATEMENT, sqlRequest);
        if (finalSQLRequest == null) {
            return;
        }
        log(sqlRequest, false);
        this.core.getAvailableSqlConnection().prepareDirectStatement(finalSQLRequest);
    }

    @Deprecated
    public ResultSet getResultSet(SQLRequest sqlRequest) {
        SQLRequest finalSQLRequest = callEvent(false, SQLRequestEvent.RequestType.RESULT_SET, sqlRequest);
        if (finalSQLRequest == null) {
            return null;
        }
        log(sqlRequest, false);
        return core.getAvailableSqlConnection().getResultSet(finalSQLRequest);
    }

    private void log(SQLRequest sqlRequest, boolean inSecondThread) {
        if (!this.enableLogs) {
            return;
        }

        this.core.getDBLogger().log(sqlRequest, identifier, inSecondThread);
    }

    private SQLRequest callEvent(boolean isInSecondThread, SQLRequestEvent.RequestType type, SQLRequest sqlRequest) {
        return this.core.getPlugin().callEvent(this, isInSecondThread, type, sqlRequest);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        this.isLoaded = loaded;
    }

    public void setPlugin(DBAPICore core) {
        this.core = core;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getTableName() {
        return this.tableName;
    }

    public long getCallCount() {
        return callCount;
    }

    public void incrementCallCount(){
        this.callCount++;
    }

    public Map<String, Integer> getCodeCallCount() {
        return codeCallCount;
    }
}
