package quarri6343.unredstone.common.logic;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import java.util.ArrayList;
import java.util.List;

import static quarri6343.unredstone.common.GlobalTeamHandler.addPlayerToTeam;
import static quarri6343.unredstone.common.GlobalTeamHandler.removePlayerFromTeam;

/**
 * ゲームが始まる前に走る処理
 */
public class GameInactiveRunnable extends BukkitRunnable {

    private int count = 0;

    private static URData getData() {
        return UnRedstone.getInstance().getData();
    }

    @Override
    public void run() {
        if (count % URData.assignTeamLength == 0) {
            assignOrUnAssignPlayersToTeam();
        }
    }

    /**
     * チームの加入エリアにいるプレイヤーを自動的にチームに加入させ、加入していないプレイヤーをチームから外す
     */
    private void assignOrUnAssignPlayersToTeam() {
        List<Player> assignedPlayers = new ArrayList<>();

        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);

            if (team.joinLocation1 == null || team.joinLocation2 == null)
                continue;

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(assignedPlayers.contains(onlinePlayer))
                    continue;

                if (UnRedstoneUtils.isPlayerInArea(onlinePlayer, team.joinLocation1, team.joinLocation2)) {
                    addPlayerToTeam(onlinePlayer, team);
                    assignedPlayers.add(onlinePlayer);
                    continue;
                }
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(!assignedPlayers.contains(onlinePlayer)){
                removePlayerFromTeam(onlinePlayer, false);
            }
        }
    }
}