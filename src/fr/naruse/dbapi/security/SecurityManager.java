package fr.naruse.dbapi.security;

import com.google.common.collect.Lists;
import fr.naruse.dbapi.main.DBAPICore;

import java.util.List;
import java.util.logging.Level;

public class SecurityManager {
    private final DBAPICore core;
    private AbstractSecurity security;
    private boolean activated = false;

    public SecurityManager(DBAPICore core) {
        this.core = core;
    }

    public void activate(Exception e) {
        if (security == null) {
            return;
        }
        this.activated = true;
        this.core.getPlugin().getLogger().log(Level.SEVERE, "An error has occurred in DBAPI.");
        this.core.getPlugin().getLogger().log(Level.INFO, "Activating the security system...");
        this.security.onError();
        this.core.getPlugin().getLogger().log(Level.INFO, "Security system activated !");

        List<String> list = Lists.newArrayList();
        list.add("**Caused by:** " + e.getClass().getName() + " | " + e.getMessage());
        list.add(" \n");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            list.add(stackTraceElement.toString());
        }
    }

    public boolean tooManyBooleansTrue(boolean... values) {
        int count = 0;
        for (boolean value : values) {
            if (value) {
                count++;
            }
        }
        return count > 1;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setSecurity(AbstractSecurity abstractSecurity) {
        this.security = abstractSecurity;
    }
}
