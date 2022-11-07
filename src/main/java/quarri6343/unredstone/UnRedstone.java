package quarri6343.unredstone;

import org.bukkit.plugin.java.JavaPlugin;
import quarri6343.unredstone.common.Config;
import quarri6343.unredstone.common.EventHandler;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.common.UnRedstoneLogic;
import quarri6343.unredstone.impl.CommandUnRedstone;

public final class UnRedstone extends JavaPlugin {

    public UnRedstoneData data;
    public Config config;
    public UnRedstoneLogic logic;
    
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
        data = new UnRedstoneData();
        config = new Config();
        config.loadConfig();
        logic = new UnRedstoneLogic();
        new CommandUnRedstone();
        new EventHandler();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        config.saveConfig();
    }
}
