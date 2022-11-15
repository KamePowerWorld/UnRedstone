package quarri6343.unredstone.common.logic;

import org.bukkit.scheduler.BukkitRunnable;
import quarri6343.unredstone.common.GlobalTeamHandler;

/**
 * ゲーム終了後時間を空けて行いたい処理
 */
public class GameEndRunnable extends BukkitRunnable {
    private final Runnable additionalAction;

    public GameEndRunnable(Runnable additionalAction) {
        this.additionalAction = additionalAction;
    }
    
    @Override
    public void run() {
        GlobalTeamHandler.teleportTeamToLobby();
        GlobalTeamHandler.resetTeams();
        additionalAction.run();
        cancel();
    }
}