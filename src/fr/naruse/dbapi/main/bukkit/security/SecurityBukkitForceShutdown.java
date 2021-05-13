package fr.naruse.dbapi.main.bukkit.security;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.security.AbstractSecurity;
import org.bukkit.Bukkit;

public class SecurityBukkitForceShutdown extends AbstractSecurity {
    public SecurityBukkitForceShutdown(DBAPICore pl) {
        super(pl);
    }

    @Override
    public void onError() {
        Bukkit.shutdown();
    }
}
