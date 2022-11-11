package quarri6343.unredstone.common;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

/**
 * コンフィグファイルを読み書きする
 */
public class ConfigHandler {

    public ConfigHandler() {
    }

    /**
     * コンフィグファイル内のデータをデータクラスにコピーする
     */
    public void loadConfig() {
        JavaPlugin plugin = UnRedstone.getInstance();
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        
        loadTeams(config);
        loadMisc(config);
    }

    /**
     * コンフィグからチームをロードする
     * @param config コンフィグ
     */
    @ParametersAreNonnullByDefault
    private void loadTeams(FileConfiguration config){
        URData data = UnRedstone.getInstance().data;
        data.teams.clearTeam();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String teamName = config.getString("team.name." + i);
            String teamColor = config.getString("team.color." + i);
            if (teamName == null || teamColor == null) {
                break;
            }
            
            data.teams.addTeam(teamName, teamColor);
            URTeam newTeam = data.teams.getTeam(i);
            newTeam.setStartLocation(config.getLocation("team.startLocation." + i));
            newTeam.setEndLocation(config.getLocation("team.endLocation." + i));
            newTeam.joinLocation1 = config.getLocation("team.joinLocation1." + i);
            newTeam.joinLocation2 = config.getLocation("team.joinLocation2." + i);
        }
    }

    /**
     * コンフィグからその他データをロードする
     * @param config コンフィグ
     */
    @ParametersAreNonnullByDefault
    private void loadMisc(FileConfiguration config){
        URData data = UnRedstone.getInstance().data;
        
        int maxHoldableItems = config.getInt("maxHoldableItems");
        if(maxHoldableItems < 1)
            maxHoldableItems = 1;
        
        data.maxHoldableItems.set(maxHoldableItems);
        
        int craftingCost = config.getInt("crafingCost");
        if(craftingCost < 1)
            craftingCost = 1;
        data.craftingCost.set(craftingCost);
    }

    /**
     * データクラスの中身をコンフィグにセーブする
     */
    public void saveConfig() {
        resetConfig();//古いデータが混在しないように一旦コンフィグを消す

        JavaPlugin plugin = UnRedstone.getInstance();
        FileConfiguration config = plugin.getConfig();

        saveTeams(config);
        saveMisc(config);

        plugin.saveConfig();
    }

    /**
     * メインクラスがロードしているチームクラスをコンフィグに保存する
     * @param config コンフィグ
     */
    @ParametersAreNonnullByDefault
    private void saveTeams(FileConfiguration config){
        URData data = UnRedstone.getInstance().data;
        for (int i = 0; i < data.teams.getTeamsLength(); i++) {
            config.set("team.name." + i, data.teams.getTeam(i).name);
            config.set("team.color." + i, data.teams.getTeam(i).color);
            config.set("team.startLocation." + i, data.teams.getTeam(i).getStartLocation());
            config.set("team.endLocation." + i, data.teams.getTeam(i).getEndLocation());
            config.set("team.joinLocation1." + i, data.teams.getTeam(i).joinLocation1);
            config.set("team.joinLocation2." + i, data.teams.getTeam(i).joinLocation2);
        }
    }
    
    @ParametersAreNonnullByDefault
    private void saveMisc(FileConfiguration config){
        URData data = UnRedstone.getInstance().data;
        config.set("maxHoldableItems", data.maxHoldableItems.get());
        config.set("crafingCost", data.craftingCost.get());
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
