package fr.naruse.dbapi.main.bungee.config;

import fr.naruse.dbapi.main.bungee.DBAPIBungeePlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class BungeeConfigFile {

    private final DBAPIBungeePlugin pl;
    private final File file;
    private final FileConfiguration configuration;

    public BungeeConfigFile(DBAPIBungeePlugin pl, String fileName) {
        this.pl = pl;
        this.file = new File(pl.getDataFolder(), fileName);
        this.configuration = new YamlConfiguration();

        try{
            if(!file.exists()){
                file.createNewFile();
                saveResource(fileName, file);
            }

            configuration.load(file);

            Reader reader = new InputStreamReader(getResource("resources/messages.yml"), "UTF8");
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            configuration.setDefaults(defConfig);
        }catch (Exception e){
            e.printStackTrace();
        }

        saveConfig();
    }

    private InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        } else {
            try {
                URL url = pl.getClass().getClassLoader().getResource(filename);
                if (url == null) {
                    return null;
                } else {
                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);
                    return connection.getInputStream();
                }
            } catch (IOException var4) {
                return null;
            }
        }
    }

    private void saveResource(String path, File messageFile) {
        try{
            InputStream inputStream = getResource(path);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            OutputStream outStream = new FileOutputStream(messageFile);
            outStream.write(buffer);

            inputStream.close();
            outStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try{
            configuration.save(file);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return configuration;
    }
}
