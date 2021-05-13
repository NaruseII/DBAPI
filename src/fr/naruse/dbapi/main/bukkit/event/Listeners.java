package fr.naruse.dbapi.main.bukkit.event;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.main.bukkit.DBAPIBukkitPlugin;
import fr.naruse.dbapi.sql.SQLConnection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class Listeners implements Listener {
    private final DBAPICore core;
    public Listeners(DBAPICore core) {
        this.core = core;
    }

    @EventHandler
    public void shutDown(PluginDisableEvent e){
        if (e.getPlugin() == DBAPIBukkitPlugin.getPlugin(DBAPIBukkitPlugin.class)) {
            for (SQLConnection sqlConnection : this.core.getSqlConnectionRegistry()) {
                sqlConnection.shutdown();
            }
        }
    }
}
