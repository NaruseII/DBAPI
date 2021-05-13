package fr.naruse.dbapi.security;

import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.sql.SQLConnection;

public class SecurityPreventRequests extends AbstractSecurity {
    public SecurityPreventRequests(DBAPICore core) {
        super(core);
    }

    @Override
    public void onError() {
        for (int i = 0; i < this.core.getSqlConnectionRegistry().getSQLConnections().size(); i++) {
            SQLConnection sqlConnection = this.core.getSqlConnectionRegistry().getSQLConnections().get(i);
            sqlConnection.getSqlThread().preventRequests();
        }
    }
}
