package quarri6343.unredstone;

import org.bukkit.plugin.java.JavaPlugin;
import quarri6343.unredstone.common.*;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.impl.command.CommandForceJoin;
import quarri6343.unredstone.impl.command.CommandForceLeave;
import quarri6343.unredstone.impl.command.CommandUnRedstone;

public final class UnRedstone extends JavaPlugin {

    public URData data;
    public ConfigHandler config;
    public URLogic logic;
    public ScoreBoardHandler scoreBoardManager;
    
    /**
     * シングルトンで管理されているこのクラスのインスタンス
     */
    private static UnRedstone instance;

    public UnRedstone() {
        instance = this;
    }

    public static UnRedstone getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        data = new URData();
        config = new ConfigHandler();
        config.loadConfig();
        logic = new URLogic();
        scoreBoardManager = new ScoreBoardHandler();
        new CommandUnRedstone();
        new CommandForceJoin();
        new CommandForceLeave();
        new EventHandler();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        config.saveConfig();
        scoreBoardManager.deleteMinecraftTeam();
    }
}
