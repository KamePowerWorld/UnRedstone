package quarri6343.unredstone;

import org.bukkit.plugin.java.JavaPlugin;
import quarri6343.unredstone.impl.CommandUnRedstone;

public final class UnRedstone extends JavaPlugin {

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
        new CommandUnRedstone();
        new EventHandler();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
