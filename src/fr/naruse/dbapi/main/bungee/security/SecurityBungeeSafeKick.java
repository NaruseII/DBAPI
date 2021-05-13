package fr.naruse.dbapi.main.bungee.security;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.main.bungee.DBAPIBungeePlugin;
import fr.naruse.dbapi.security.AbstractSecurity;
import fr.naruse.dbapi.sql.SQLConnection;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SecurityBungeeSafeKick extends AbstractSecurity implements Listener {
    private boolean isActive = false;

    public SecurityBungeeSafeKick(DBAPICore pl, DBAPIBungeePlugin plugin) {
        super(pl);
        BungeeCord.getInstance().getPluginManager().registerListener(plugin, this);
    }

    @Override
    public void onError() {
        this.isActive = true;
        for (int i = 0; i < core.getSqlConnectionRegistry().getSQLConnections().size(); i++) {
            SQLConnection sqlConnection = core.getSqlConnectionRegistry().getSQLConnections().get(i);
            sqlConnection.getSqlThread().preventRequests();
        }
        for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
            player.disconnect("Unexpected error.");
        }
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        if (isActive) {
            e.setCancelled(true);
            e.setCancelReason("Serveur en maintenance.");
        }
    }
}
