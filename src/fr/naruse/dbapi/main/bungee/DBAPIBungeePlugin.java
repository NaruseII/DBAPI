package fr.naruse.dbapi.main.bungee;

import fr.naruse.dbapi.api.event.SQLRequestEvent;
import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.main.IDBAPIPlugin;
import fr.naruse.dbapi.main.bungee.cmd.DBBungeeCommands;
import fr.naruse.dbapi.main.bungee.config.BungeeConfigFile;
import fr.naruse.dbapi.main.bungee.security.SecurityBungeeForceKick;
import fr.naruse.dbapi.main.bungee.security.SecurityBungeeForceShutdown;
import fr.naruse.dbapi.main.bungee.security.SecurityBungeeSafeKick;
import fr.naruse.dbapi.main.bungee.security.SecurityBungeeSafeShutdown;
import fr.naruse.dbapi.security.SecurityPreventRequests;
import fr.naruse.dbapi.sql.SQLRequest;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class DBAPIBungeePlugin extends Plugin implements IDBAPIPlugin, Listener {

    private final DBAPICore CORE = new DBAPICore(false);
    private BungeeConfigFile bungeeConfig;

    @Override
    public void onLoad() {
        super.onLoad();
        this.bungeeConfig = new BungeeConfigFile(this, "config.yml");
        CORE.setAvailableConnections(bungeeConfig.getConfig().getInt("availableConnections"));
        CORE.setRequestSeparator(bungeeConfig.getConfig().getInt("requestSeparator"));
        CORE.onLoad(this);
        addSecurityManager();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        CORE.onEnable();

        getProxy().getPluginManager().registerCommand(this, new DBBungeeCommands(CORE, this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        CORE.onDisable();
    }

    @Override
    public <T> T getConfigObject(String path) {
        return (T) bungeeConfig.getConfig().get(path);
    }

    @Override
    public void sendConsoleMessage(String msg) {
        BungeeCord.getInstance().getConsole().sendMessage(msg);
    }

    @Override
    public void scheduleTask(Runnable runnable, long ticks) {
        BungeeCord.getInstance().getScheduler().schedule(this, runnable, ticks / 20, TimeUnit.SECONDS);
    }

    @Override
    public SQLRequest callEvent(Database database, boolean isInSecondThread, SQLRequestEvent.RequestType type, SQLRequest sqlRequest) {
        return sqlRequest;
    }

    @Override
    public String getServerName() {
        return "Waterfall";
    }

    private void addSecurityManager() {
        boolean preventRequests = bungeeConfig.getConfig().getBoolean("security.preventRequests");
        boolean forceKick = bungeeConfig.getConfig().getBoolean("security.forceKick");
        boolean safeKick = bungeeConfig.getConfig().getBoolean("security.safeKick");
        boolean forceShutdown = bungeeConfig.getConfig().getBoolean("security.forceShutdown");
        boolean safeShutdown = bungeeConfig.getConfig().getBoolean("security.safeShutdown");
        if (CORE.getSecurityManager().tooManyBooleansTrue(preventRequests, forceKick, safeKick, forceShutdown, safeShutdown)) {
            throw new Error("Only 1 security system can be activated !");
        }
        if (preventRequests) {
            CORE.getSecurityManager().setSecurity(new SecurityPreventRequests(CORE));
        }
        if (forceKick) {
            CORE.getSecurityManager().setSecurity(new SecurityBungeeForceKick(CORE, this));
        }
        if (safeKick) {
            CORE.getSecurityManager().setSecurity(new SecurityBungeeSafeKick(CORE, this));
        }
        if (forceShutdown) {
            CORE.getSecurityManager().setSecurity(new SecurityBungeeForceShutdown(CORE));
        }
        if (safeShutdown) {
            CORE.getSecurityManager().setSecurity(new SecurityBungeeSafeShutdown(CORE));
        }
    }

    public DBAPICore getCore() {
        return this.CORE;
    }
}
