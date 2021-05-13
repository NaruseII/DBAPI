package fr.naruse.dbapi.main.bungee.security;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.security.AbstractSecurity;
import net.md_5.bungee.BungeeCord;

public class SecurityBungeeForceShutdown extends AbstractSecurity {
    public SecurityBungeeForceShutdown(DBAPICore core) {
        super(core);
    }

    @Override
    public void onError() {
        BungeeCord.getInstance().stop();
    }
}
