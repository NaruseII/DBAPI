package fr.naruse.dbapi.main.bukkit;

import fr.naruse.dbapi.api.event.SQLRequestEvent;
import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.main.IDBAPIPlugin;
import fr.naruse.dbapi.main.bukkit.cmd.DBBukkitCommands;
import fr.naruse.dbapi.main.bukkit.event.Listeners;
import fr.naruse.dbapi.main.bukkit.security.SecurityBukkitForceKick;
import fr.naruse.dbapi.main.bukkit.security.SecurityBukkitForceShutdown;
import fr.naruse.dbapi.main.bukkit.security.SecurityBukkitSafeKick;
import fr.naruse.dbapi.main.bukkit.security.SecurityBukkitSafeShutdown;
import fr.naruse.dbapi.security.SecurityPreventRequests;
import fr.naruse.dbapi.sql.SQLRequest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DBAPIBukkitPlugin extends JavaPlugin implements IDBAPIPlugin {

    private final DBAPICore CORE = new DBAPICore(true);

    @Override
    public void onLoad() {
        super.onLoad();
        CORE.setAvailableConnections(getConfig().getInt("availableConnections"));
        CORE.setRequestSeparator(getConfig().getInt("requestSeparator"));
        CORE.onLoad(this);
        addSecurityManager();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }
        CORE.onEnable();

        getCommand("dbapi").setExecutor(new DBBukkitCommands(CORE, this));

        Bukkit.getPluginManager().registerEvents(new Listeners(CORE), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        CORE.onDisable();
    }

    @Override
    public <T> T getConfigObject(String path) {
        return (T) getConfig().get(path);
    }

    @Override
    public void sendConsoleMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    @Override
    public void scheduleTask(Runnable runnable, long ticks) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> runnable.run(), ticks);
    }

    @Override
    public SQLRequest callEvent(Database database, boolean isInSecondThread, SQLRequestEvent.RequestType type, SQLRequest sqlRequest) {
        SQLRequestEvent sqlRequestEvent = new SQLRequestEvent(isInSecondThread, type, database, sqlRequest);
        Bukkit.getPluginManager().callEvent(sqlRequestEvent);
        return sqlRequestEvent.getSqlRequest();
    }

    @Override
    public String getServerName() {
        return Bukkit.getServerName();
    }

    private void addSecurityManager() {
        boolean preventRequests = getConfig().getBoolean("security.preventRequests");
        boolean forceKick = getConfig().getBoolean("security.forceKick");
        boolean safeKick = getConfig().getBoolean("security.safeKick");
        boolean forceShutdown = getConfig().getBoolean("security.forceShutdown");
        boolean safeShutdown = getConfig().getBoolean("security.safeShutdown");
        if (CORE.getSecurityManager().tooManyBooleansTrue(preventRequests, forceKick, safeKick, forceShutdown, safeShutdown)) {
            throw new Error("Only 1 security system can be activated !");
        }
        if (preventRequests) {
            CORE.getSecurityManager().setSecurity(new SecurityPreventRequests(CORE));
        }
        if (forceKick) {
            CORE.getSecurityManager().setSecurity(new SecurityBukkitForceKick(CORE));
        }
        if (safeKick) {
            CORE.getSecurityManager().setSecurity(new SecurityBukkitSafeKick(CORE));
        }
        if (forceShutdown) {
            CORE.getSecurityManager().setSecurity(new SecurityBukkitForceShutdown(CORE));
        }
        if (safeShutdown) {
            CORE.getSecurityManager().setSecurity(new SecurityBukkitSafeShutdown(CORE));
        }
    }

    public DBAPICore getCore() {
        return this.CORE;
    }
}
