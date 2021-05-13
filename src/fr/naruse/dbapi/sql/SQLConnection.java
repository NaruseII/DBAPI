package fr.naruse.dbapi.sql;

import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.util.Message;

import java.sql.*;

import java.util.logging.Level;

public class SQLConnection {
    private final SQLThread sqlThread = new SQLThread(this);
    private final String urlBase;
    private final String host;
    private final String bdd;
    private final String user;
    private final String pass;
    private final DBAPICore core;
    private final String connectionName;
    private Connection connection;
    private Statement statement;

    public SQLConnection(DBAPICore core, String urlBase, String host, String database, String user, String pass, int i) {
        this.core = core;
        this.urlBase = urlBase;
        this.host = host;
        this.bdd = database;
        this.user = user;
        this.pass = pass;
        this.connectionName = "§5§l[§6Connection " + i + "§5§l]";
        this.sqlThread.init();
    }

    public void connection(boolean secondThread) {
        if (secondThread) {
            this.sqlThread.addToQueue(() -> {
                disconnection(false);
                connection();
            }, true, null);
        } else {
            disconnection(false);
            connection();
        }
    }

    private void connection() {
        try {
            this.connection = DriverManager.getConnection(this.urlBase + this.host + "/" + this.bdd + "?&useSSL=false&useTimezone=true&serverTimezone=GMT", this.user, this.pass);
            core.getPlugin().sendConsoleMessage(Message.B.getMessage() + " " + this.connectionName + " §aSuccessful connection.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnection(boolean secondThread) {
        if (secondThread) {
            this.sqlThread.addToQueue(() -> {
                try {

                    if (this.connection != null) {
                        this.connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }, null);
        } else {
            try {

                if (this.connection != null) {
                    this.connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void initDatabase(Database database) {
        this.sqlThread.addToQueue(() -> {
            try {
                core.getPlugin().sendConsoleMessage(Message.B.getMessage() + " " + this.connectionName + " §aInitializing database §2'" + database.getIdentifier() + "'§a...");
                DatabaseMetaData dbm = this.connection.getMetaData();
                ResultSet tables = dbm.getTables(null, null, database.getTableName(), null);

                if (!tables.next() && !database.getQuery().equals("nope")) {
                    this.statement = this.connection.createStatement();

                    String query = database.getQuery();

                    this.statement.executeUpdate(query);
                    core.getPlugin().sendConsoleMessage(Message.B.getMessage() + " " + this.connectionName + " §aTable created for §2'" + database.getIdentifier() + "'");
                }

                core.getPlugin().sendConsoleMessage(Message.B.getMessage() + " " + this.connectionName + " §aTable loaded for §2'" + database.getIdentifier() + "'");

                database.setLoaded(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, null);
    }

    public void prepareStatement(SQLRequest sqlRequest, SQLResponse response, Database database) {
        this.sqlThread.addToQueue(() -> {
            try {
                PreparedStatement q = this.connection.prepareStatement(sqlRequest.getSqlRequest());
                sqlRequest.setValues(q);
                q.execute();
                q.close();
                if (response != null) {
                    response.runSynchronously(core, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                error(sqlRequest, database);
            }
        }, database);
    }

    public SQLResponse hasAccount(SQLRequest sqlRequest, SQLResponse asyncResponse, Database database) {
        this.sqlThread.addToQueue(() -> {
            try {
                PreparedStatement q = this.connection.prepareStatement(sqlRequest.getSqlRequest());
                sqlRequest.setValues(q);
                ResultSet result = q.executeQuery();
                boolean hasAccount = result.next();
                q.close();
                asyncResponse.runSynchronously(core, hasAccount);
            } catch (SQLException e) {
                e.printStackTrace();
                asyncResponse.runSynchronously(core, null);
                error(sqlRequest, database);
            }
        }, database);
        return asyncResponse;
    }

    public SQLResponse getObject(SQLRequest.GetObject sqlRequest, SQLResponse asyncResponse, Database database) {
        sqlThread.addToQueue(() -> {
            try {
                PreparedStatement q = this.connection.prepareStatement(sqlRequest.getSqlRequest());
                sqlRequest.setValues(q);
                ResultSet result = q.executeQuery();
                Object o = null;
                while (result.next()) {
                    o = result.getObject(sqlRequest.getColumnName());
                }
                q.close();
                asyncResponse.runSynchronously(core, o);
            } catch (SQLException e) {
                e.printStackTrace();
                error(sqlRequest, database);
            }
        }, database);
        return asyncResponse;
    }

    public SQLResponse getResultSet(SQLRequest sqlRequest, SQLResponse response, Database database) {
        sqlThread.addToQueue(() -> {

            try {
                PreparedStatement q = this.connection.prepareStatement(sqlRequest.getSqlRequest());
                sqlRequest.setValues(q);
                ResultSet resultSet = q.executeQuery();
                response.runSynchronously(core, resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
                error(sqlRequest, database);
                response.runSynchronously(core, null);
            }
        }, database);
        return response;
    }

    @Deprecated
    public Object getObject(SQLRequest.GetObject sqlRequest) {
        try {
            PreparedStatement q = this.connection.prepareStatement(sqlRequest.getSqlRequest());
            sqlRequest.setValues(q);
            ResultSet result = q.executeQuery();
            Object o = null;
            while (result.next()) {
                o = result.getObject(sqlRequest.getColumnName());
            }
            q.close();
            return o;
        } catch (SQLException e) {
            e.printStackTrace();
            error(sqlRequest);
        }
        return null;
    }

    @Deprecated
    public boolean hasAccount(SQLRequest sqlRequest) {
        try {
            PreparedStatement q = this.connection.prepareStatement(sqlRequest.getSqlRequest());
            sqlRequest.setValues(q);
            ResultSet result = q.executeQuery();
            boolean hasAccount = result.next();
            q.close();
            return hasAccount;
        } catch (SQLException e) {
            e.printStackTrace();
            error(sqlRequest);
            return false;
        }
    }

    @Deprecated
    public void prepareDirectStatement(SQLRequest sqlRequest) {
        try {
            PreparedStatement q = this.connection.prepareStatement(sqlRequest.getSqlRequest());
            sqlRequest.setValues(q);
            q.execute();
            q.close();
        } catch (Exception e) {
            e.printStackTrace();
            error(sqlRequest);
        }
    }

    @Deprecated
    public ResultSet getResultSet(SQLRequest sqlRequest) {
        try {
            PreparedStatement q = this.connection.prepareStatement(sqlRequest.getSqlRequest());
            sqlRequest.setValues(q);
            return q.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            error(sqlRequest);
            return null;
        }
    }

    public void error(SQLRequest sqlRequest) {
        error(sqlRequest, null);
    }

    public void error(SQLRequest sqlRequest, Database database) {
        String request = this.connectionName + " [ERROR] Query: " + sqlRequest.getSqlRequest() + " Arguments: " + sqlRequest.getObjects();
        core.getPlugin().getLogger().log(Level.SEVERE, request);
        if (database != null) {
            String databaseString = this.connectionName + " [ERROR] Error from database with id: " + database.getIdentifier();
            core.getPlugin().getLogger().log(Level.SEVERE, databaseString);
        }
        //Bukkit.getConsoleSender().sendMessage(Message.B.getMessage() + " " + this.connectionName + " §4[ERROR] §eQuery: §c" + sqlRequest.getSqlRequest() + " §eArguments: §c" + sqlRequest.getObjects());
    }

    public SQLThread getSqlThread() {
        return this.sqlThread;
    }

    public DBAPICore getCore() {
        return this.core;
    }

    public boolean isClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void shutdown() {
        getSqlThread().shutdown();
        disconnection(false);
        core.getPlugin().sendConsoleMessage(Message.B.getMessage() + " " + this.connectionName + " §aSuccessful disconnection.");
    }
}


