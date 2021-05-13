package fr.naruse.dbapi.main.bukkit.security;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.main.bukkit.DBAPIBukkitPlugin;
import fr.naruse.dbapi.security.AbstractSecurity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class SecurityBukkitForceKick extends AbstractSecurity implements Listener {
    private boolean isActive = false;

    public SecurityBukkitForceKick(DBAPICore pl) {
        super(pl);
        Bukkit.getPluginManager().registerEvents(this, DBAPIBukkitPlugin.getPlugin(DBAPIBukkitPlugin.class));
    }

    @Override
    public void onError() {
        this.isActive = true;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.kickPlayer("Unexpected error.");
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (isActive) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server closed. Cause: DBAPI");
        }
    }
}
