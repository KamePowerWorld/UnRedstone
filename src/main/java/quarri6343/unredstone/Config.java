package quarri6343.unredstone;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Config {
    public UnRedstoneData data;

    public Config() {
    }

    public void loadConfig() {
        JavaPlugin plugin = UnRedstone.getInstance();
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        data = new UnRedstoneData();
        data.startLocation = config.getLocation("data.startLocation");
        data.endLocation = config.getLocation("data.endLocation");
    }

    public void saveConfig() {
        JavaPlugin plugin = UnRedstone.getInstance();
        FileConfiguration config = plugin.getConfig();

        config.set("data.startLocation", data.startLocation);
        config.set("data.endLocation", data.endLocation);

        plugin.saveConfig();
    }
}
