package quarri6343.unredstone.common;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import quarri6343.unredstone.UnRedstone;

import java.io.File;

/**
 * コンフィグファイルを読み書きする
 */
public class Config {

    public Config() {
    }

    /**
     * コンフィグファイル内のデータをデータクラスにコピーする
     */
    public void loadConfig() {
        JavaPlugin plugin = UnRedstone.getInstance();
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        UnRedstoneData data = UnRedstone.getInstance().data;

        data.clearTeam();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (config.getString("team.name." + i) == null || config.getString("team.color." + i) == null) {
                break;
            }
            data.addTeam(config.getString("team.name." + i), config.getString("team.color." + i));
            data.getTeam(i).startLocation = config.getLocation("team.startLocation." + i);
            data.getTeam(i).endLocation = config.getLocation("team.endLocation." + i);
            data.getTeam(i).joinLocation1 = config.getLocation("team.joinlocation1." + i);
            data.getTeam(i).joinLocation2 = config.getLocation("team.joinlocation2." + i);
        }
    }

    /**
     * データクラスの中身をコンフィグにセーブする
     */
    public void saveConfig() {
        resetConfig();//古いデータが混在しないように一旦コンフィグを消す

        JavaPlugin plugin = UnRedstone.getInstance();
        FileConfiguration config = plugin.getConfig();
        UnRedstoneData data = UnRedstone.getInstance().data;

        for (int i = 0; i < data.getTeamsLength(); i++) {
            config.set("team.name." + i, data.getTeam(i).name);
            config.set("team.color." + i, data.getTeam(i).color);
            config.set("team.startLocation." + i, data.getTeam(i).startLocation);
            config.set("team.endLocation." + i, data.getTeam(i).endLocation);
            config.set("team.joinlocation1." + i, data.getTeam(i).joinLocation1);
            config.set("team.joinlocation2." + i, data.getTeam(i).joinLocation2);
        }

        plugin.saveConfig();
    }

    /**
     * コンフィグを全て削除する
     */
    public void resetConfig() {
        JavaPlugin plugin = UnRedstone.getInstance();
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (configFile.delete()) {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
        }
    }
}
