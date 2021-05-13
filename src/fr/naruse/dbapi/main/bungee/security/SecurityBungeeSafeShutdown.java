package fr.naruse.dbapi.main.bungee.security;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.security.AbstractSecurity;
import fr.naruse.dbapi.sql.SQLConnection;
import net.md_5.bungee.BungeeCord;

public class SecurityBungeeSafeShutdown extends AbstractSecurity {
    public SecurityBungeeSafeShutdown(DBAPICore core) {
        super(core);
    }

    @Override
    public void onError() {
        for (int i = 0; i < core.getSqlConnectionRegistry().getSQLConnections().size(); i++) {
            SQLConnection sqlConnection = core.getSqlConnectionRegistry().getSQLConnections().get(i);
            sqlConnection.getSqlThread().preventRequests();
        }
        BungeeCord.getInstance().stop();
    }
}
