package quarri6343.unredstone;

import org.bukkit.plugin.java.JavaPlugin;
import quarri6343.unredstone.common.ConfigHandler;
import quarri6343.unredstone.common.EventHandler;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.impl.command.CommandForceJoin;
import quarri6343.unredstone.impl.command.CommandForceLeave;
import quarri6343.unredstone.impl.command.CommandUnRedstone;

public final class UnRedstone extends JavaPlugin {

    private URData data;
    private URLogic logic;

    private ConfigHandler config;

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
        data = new URData();
        config = new ConfigHandler();
        config.loadConfig();
        logic = new URLogic();
        new CommandUnRedstone();
        new CommandForceJoin();
        new CommandForceLeave();
        new EventHandler();
    }

    @Override
    public void onDisable() {
        config.saveConfig();
        
        if (logic.gameStatus != URLogic.GameStatus.INACTIVE) {
            logic.endGame(null, null, URLogic.GameResult.FAIL, false);
        }
    }

    public URData getData() {
        return data;
    }

    public URLogic getLogic() {
        return logic;
    }
}
