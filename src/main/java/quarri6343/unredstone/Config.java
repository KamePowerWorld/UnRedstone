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
        for (int i = 0; i < UnRedstone.maxLoadableTeams; i++) {
            data.startLocation[i] = config.getLocation("data.startLocation." + i);
            data.endLocation[i] = config.getLocation("data.endLocation." + i);
        }
    }

    public void saveConfig() {
        JavaPlugin plugin = UnRedstone.getInstance();
        FileConfiguration config = plugin.getConfig();

        for (int i = 0; i < UnRedstone.maxLoadableTeams; i++) {
            config.set("data.startLocation." + i, data.startLocation[i]);
            config.set("data.endLocation." + i, data.endLocation[i]);
        }
        
        plugin.saveConfig();
    }
}
