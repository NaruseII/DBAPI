package fr.naruse.dbapi.main.bukkit.security;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.security.AbstractSecurity;
import fr.naruse.dbapi.sql.SQLConnection;
import org.bukkit.Bukkit;

public class SecurityBukkitSafeShutdown extends AbstractSecurity {
    public SecurityBukkitSafeShutdown(DBAPICore pl) {
        super(pl);
    }

    @Override
    public void onError() {
        for (int i = 0; i < core.getSqlConnectionRegistry().getSQLConnections().size(); i++) {
            SQLConnection sqlConnection = core.getSqlConnectionRegistry().getSQLConnections().get(i);
            sqlConnection.getSqlThread().preventRequests();
        }
        Bukkit.shutdown();
    }
}
