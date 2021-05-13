package fr.naruse.dbapi.security;

import fr.naruse.dbapi.main.DBAPICore;

public abstract class AbstractSecurity {
    protected DBAPICore core;

    public AbstractSecurity(DBAPICore core) {
        this.core = core;
    }

    public abstract void onError();
}
