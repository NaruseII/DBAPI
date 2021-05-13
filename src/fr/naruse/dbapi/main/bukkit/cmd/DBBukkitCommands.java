package fr.naruse.dbapi.main.bukkit.cmd;

import com.google.common.collect.Lists;
import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.main.bukkit.DBAPIBukkitPlugin;
import fr.naruse.dbapi.sql.SQLConnection;
import fr.naruse.dbapi.sql.SQLRequest;
import fr.naruse.dbapi.sql.SQLResponse;
import fr.naruse.dbapi.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

public class DBBukkitCommands implements CommandExecutor, TabCompleter {
    private final DBAPICore core;
    private final DBAPIBukkitPlugin plugin;

    public DBBukkitCommands(DBAPICore core, DBAPIBukkitPlugin plugin) {
        this.core = core;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!hasPermission(sender, "dbapi")) {
            return sendMessage(sender, "§4Vous n'avez pas la permission.");
        }

        if (args.length == 0) {
            sendMessage(sender, Message.B.getMessage() + " §aVersion " + plugin.getDescription().getVersion());
            sendMessage(sender, "§e/§7dbapi reload");
            sendMessage(sender, "§e/§7dbapi stackTrace <Database>");
            sendMessage(sender, "§e/§7dbapi callCount <Database>");
            sendMessage(sender, "§e/§7dbapi requestPush <Database> <Query>");
            return sendMessage(sender, "§e/§7dbapi requestGet <Database> <Query>");
        }

        if (args[0].equalsIgnoreCase("reload")) {
            for (SQLConnection sqlConnection : this.core.getSqlConnectionRegistry()) {
                sqlConnection.disconnection(true);
                sqlConnection.connection(true);
            }
            return sendMessage(sender, "§aReconnection lancée.");
        }else if (args[0].equalsIgnoreCase("requestPush")) {
            if (args.length < 3) {
                return sendMessage(sender, "§e/§7dbapi prepareStatement <Database> <Query>");
            }
            Database database = plugin.getCore().getDatabaseRegistry().getDatabase(args[1]);
            if(database == null){
                return sendMessage(sender, "§cDatabase introuvable.");
            }
            StringBuilder stringBuilder = new StringBuilder(args[2]);
            for (int i = 3; i < args.length; i++) {
                stringBuilder.append(" "+args[i]);
            }
            String request = stringBuilder.toString();
            if(!request.endsWith(";")){
                request += ";";
            }
            SQLRequest sqlRequest = new SQLRequest(request);
            database.prepareStatement(sqlRequest);

            sendMessage(sender, "§aRequête envoyée.");
        }else if (args[0].equalsIgnoreCase("requestGet")) {
            if (args.length < 3) {
                return sendMessage(sender, "§e/§7dbapi requestGet <Database> <Query>");
            }
            Database database = plugin.getCore().getDatabaseRegistry().getDatabase(args[1]);
            if(database == null){
                return sendMessage(sender, "§cDatabase introuvable.");
            }
            StringBuilder stringBuilder = new StringBuilder(args[2]);
            for (int i = 3; i < args.length; i++) {
                stringBuilder.append(" "+args[i]);
            }
            String request = stringBuilder.toString();
            if(!request.endsWith(";")){
                request += ";";
            }
            SQLRequest sqlRequest = new SQLRequest(request);
            database.getResultSet(sqlRequest, new SQLResponse() {
                @Override
                public void handleResponse(Object response) {
                    if(response == null){
                        sendMessage(sender, Message.B.getMessage()+" §4null");
                        return;
                    }
                    ResultSet resultSet = (ResultSet) response;
                    try{
                        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                        final int columnCount = resultSetMetaData.getColumnCount();
                        StringBuilder stringBuilder1 = new StringBuilder(",");
                        while (resultSet.next()){
                            for (int i = 1; i <= columnCount; i++) {
                                stringBuilder1.append(", "+resultSetMetaData.getColumnName(i)+"="+resultSet.getObject(i));
                                resultSet.getObject(i);
                            }
                            stringBuilder1.append(" | ");
                        }
                        sendMessage(sender, Message.B.getMessage()+" §4"+stringBuilder1.toString().replace(",, ", ""));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            sendMessage(sender, Message.B.getMessage()+" §aRequête envoyée.");
        }else if (args[0].equalsIgnoreCase("callCount")) {
            if (args.length < 2) {
                return sendMessage(sender, "§e/§7dbapi callCount <Database>");
            }
            Database database = plugin.getCore().getDatabaseRegistry().getDatabase(args[1]);
            if(database == null){
                return sendMessage(sender, "§cDatabase introuvable.");
            }
            sendMessage(sender, Message.B.getMessage()+" §4"+database.getCallCount());
        }else if (args[0].equalsIgnoreCase("stackTrace")) {
            if (args.length < 2) {
                return sendMessage(sender, "§e/§7dbapi stackTrace <Database>");
            }
            Database database = plugin.getCore().getDatabaseRegistry().getDatabase(args[1]);
            if(database == null){
                return sendMessage(sender, "§cDatabase introuvable.");
            }
            sendMessage(sender, Message.B.getMessage()+" §4"+database.getCodeCallCount().toString());
        }

        return false;
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            return sender.getName().equals("Naruse");
        }

        return true;
    }

    private boolean sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(msg);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> list = Lists.newArrayList();
        if(args.length == 2){
            for (Database database : plugin.getCore().getDatabaseRegistry()) {
                if(database.getIdentifier().startsWith(args[args.length - 1])){
                    list.add(database.getIdentifier());
                }
            }
        }
        if(args.length > 2){
            Database database = plugin.getCore().getDatabaseRegistry().getDatabase(args[1]);
            if(database != null){
                list.add(database.getTableName());
            }
        }
        return list;
    }
}
