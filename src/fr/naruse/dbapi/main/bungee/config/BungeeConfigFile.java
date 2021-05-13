package fr.naruse.dbapi.main.bungee.config;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class BungeeConfigFile
{
    private Plugin plugin;
    private File file;
    private Configuration fileConfiguration;

    public BungeeConfigFile(Plugin plugin, File file)
    {
        this.plugin = plugin;
        this.file = file;
        setup();
    }

    public BungeeConfigFile(Plugin plugin, String name)
    {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name);
        setup();
    }

    public String getName()
    {
        return file.getName();
    }

    private void setup()
    {
        try
        {
            if(!this.file.getParentFile().exists())
                this.file.getParentFile().mkdirs();

            if(!this.file.exists())
                this.file.createNewFile();

            this.fileConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
        }
        catch (IOException localIOException)
        {
            localIOException.printStackTrace();
        }
    }

    public void reload()
    {
        setup();
    }

    public Configuration getConfig()
    {
        if (this.fileConfiguration == null) {
            reload();
        }
        return this.fileConfiguration;
    }

    public void save()
    {
        try
        {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.fileConfiguration, file);
        }
        catch (IOException localIOException)
        {
            localIOException.printStackTrace();
        }
    }

    public void saveDefaultConfig()
    {
        if (!file.exists() || file.length() == 0) {
            file.delete();
            try
            {
                Files.copy(this.plugin.getResourceAsStream(file.getName()), file.toPath(), new CopyOption[0]);
            }
            catch (IOException localIOException)
            {
                localIOException.printStackTrace();
            }
        }
    }

}
