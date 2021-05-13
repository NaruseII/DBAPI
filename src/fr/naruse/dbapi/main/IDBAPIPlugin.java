package fr.naruse.dbapi.main;

import fr.naruse.dbapi.api.event.SQLRequestEvent;
import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.sql.SQLRequest;

import java.util.logging.Logger;

public interface IDBAPIPlugin {

    <T> T getConfigObject(String path);

    void sendConsoleMessage(String msg);

    void scheduleTask(Runnable runnable, long ticks);

    Logger getLogger();

    SQLRequest callEvent(Database database, boolean isInSecondThread, SQLRequestEvent.RequestType type, SQLRequest sqlRequest);

    String getServerName();
}
