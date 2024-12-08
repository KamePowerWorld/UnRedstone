package quarri6343.unredstone.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import quarri6343.unredstone.common.data.URTeam;

public class MCTeams {

    /**
     * 新しいminecraftのチームを作る
     */
    public static Team createMinecraftTeam(URTeam urTeam) {
        Team team = getBoard().getTeam(urTeam.name);

        if (team == null) {
            team = getBoard().registerNewTeam(urTeam.name);
            team.color(NamedTextColor.NAMES.value(urTeam.color));
            team.displayName(Component.text(urTeam.name).color(NamedTextColor.NAMES.value(urTeam.color)));
            team.setAllowFriendlyFire(false);
        }

        return team;
    }

    /**
     * プレイヤーのチームを取得
     *
     * @return チーム
     */
    public static Team getPlayerTeam(Player player) {
        return getBoard().getEntryTeam(player.getName());
    }

    private static Scoreboard getBoard() {
        return Bukkit.getScoreboardManager().getMainScoreboard();
    }
}
