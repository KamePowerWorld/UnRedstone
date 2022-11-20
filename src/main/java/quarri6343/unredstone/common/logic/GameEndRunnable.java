package quarri6343.unredstone.common.logic;

import org.bukkit.scheduler.BukkitRunnable;
import quarri6343.unredstone.common.GlobalTeamHandler;

/**
 * ゲーム終了後時間を空けて行いたい処理
 */
public class GameEndRunnable extends BukkitRunnable {
    private final Runnable additionalAction;
    private final boolean isScheduled;

    public GameEndRunnable(Runnable additionalAction, boolean isScheduled) {
        this.additionalAction = additionalAction;
        this.isScheduled = isScheduled;
    }
    
    @Override
    public void run() {
        GlobalTeamHandler.teleportTeamToLobby();
        GlobalTeamHandler.resetTeams();
        additionalAction.run();
        if(isScheduled)
            cancel();
    }
}