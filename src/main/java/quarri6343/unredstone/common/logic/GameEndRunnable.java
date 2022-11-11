package quarri6343.unredstone.common.logic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.utils.UnRedstoneUtils;

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
        teleportTeamToLobby();
        UnRedstone.getInstance().scoreBoardManager.deleteMinecraftTeam();
        getData().teams.disbandTeams();
        additionalAction.run();
        cancel();
    }

    /**
     * チームメンバーをチームに加入した位置にテレポートさせる
     */
    private void teleportTeamToLobby() {
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            if (team.joinLocation1 == null || team.joinLocation2 == null)
                continue;

            Location centerLocation = UnRedstoneUtils.getCenterLocation(team.joinLocation1, team.joinLocation2);
            for (Player player : team.players) {
                player.teleport(centerLocation);
            }
        }
    }

    private URData getData() {
        return UnRedstone.getInstance().data;
    }
}