package fr.naruse.dbapi.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.naruse.dbapi.database.Database;
import fr.naruse.dbapi.main.DBAPICore;
import fr.naruse.dbapi.util.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DatabaseRegistry implements Iterable<Database> {
    private final ArrayList<Database> databases = Lists.newArrayList();
    private final HashMap<String, Database> databaseHashMap = Maps.newHashMap();

    public void add(DBAPICore core, Database database) {
        this.databases.add(database);
        this.databaseHashMap.put(database.getIdentifier(), database);

        core.getPlugin().sendConsoleMessage(Message.B.getMessage() + " §aRegistering database §2'" + database.getIdentifier() + "'");
    }

    public void remove(String identifier) {
        if (this.databaseHashMap.containsKey(identifier)) {
            this.databases.remove(this.databaseHashMap.get(identifier));
            this.databaseHashMap.remove(identifier);
        }
    }

    public Database getDatabase(String identifier) {
        return this.databaseHashMap.get(identifier);
    }

    public Set<String> getIdentifiers() {
        return this.databaseHashMap.keySet();
    }

    @Override
    public Iterator<Database> iterator() {
        return this.databases.iterator();
    }
}
