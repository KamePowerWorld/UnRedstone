package quarri6343.unredstone.common;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import quarri6343.unredstone.UnRedstone;

public class ScoreBoardManager {

    /**
     * データに存在するチームからminecraftのチームを作る
     */
    public void createTeam() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();

        for (int i = 0; i < getData().getTeamsLength(); i++) {
            if (getData().getTeam(i).players.size() == 0)
                continue;

            Team team = board.registerNewTeam(getData().getTeam(i).name);
            team.color(NamedTextColor.NAMES.value(getData().getTeam(i).color));
            team.setPrefix(ChatColor.RED.toString());
            team.setSuffix(ChatColor.RESET.toString());
            team.setDisplayName(getData().getTeam(i).name);
            team.setAllowFriendlyFire(false);

            for (Player player : getData().getTeam(i).players) {
                team.addPlayer(player);
            }
        }
    }

    /**
     * minecraftのチームを全て解散させる
     */
    public void deleteTeam() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();

        for (int i = 0; i < getData().getTeamsLength(); i++) {
            Team team = board.getTeam(getData().getTeam(i).name);
            if (team != null)
                team.unregister();
        }
    }

    private UnRedstoneData getData() {
        return UnRedstone.getInstance().data;
    }
}
