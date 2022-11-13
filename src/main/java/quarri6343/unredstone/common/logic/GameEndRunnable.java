package quarri6343.unredstone.common.logic;

import org.bukkit.scheduler.BukkitRunnable;
import quarri6343.unredstone.UnRedstone;

/**
 * ゲーム終了後時間を空けて行いたい処理
 */
public class GameEndRunnable extends BukkitRunnable {
    private final Runnable additionalAction;
    
    public GameEndRunnable(Runnable additionalAction){
        this.additionalAction = additionalAction;
    }
    
    @Override
    public void run() {
        UnRedstone.getInstance().globalTeamHandler.teleportTeamToLobby();
        UnRedstone.getInstance().globalTeamHandler.resetTeams();
        additionalAction.run();
        cancel();
    }
}