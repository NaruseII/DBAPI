package fr.naruse.dbapi.api.event;

import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.sql.SQLRequest;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SQLRequestEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final boolean isInSecondThread;
    private final RequestType requestType;
    private final Database database;
    private boolean isCancelled = false;
    private SQLRequest sqlRequest;

    public SQLRequestEvent(boolean isInSecondThread, RequestType type, Database database, SQLRequest sqlRequest) {
        this.isInSecondThread = isInSecondThread;
        this.requestType = type;
        this.database = database;
        this.sqlRequest = sqlRequest;
    }

    public Database getDatabase() {
        return database;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public boolean isInSecondThread() {
        return isInSecondThread;
    }

    public SQLRequest getSqlRequest() {
        return sqlRequest;
    }

    public void setSqlRequest(SQLRequest sqlRequest) {
        this.sqlRequest = sqlRequest;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum RequestType {
        PREPARE_STATEMENT, GET_OBJECT, EXISTS, RESULT_SET
    }
}
