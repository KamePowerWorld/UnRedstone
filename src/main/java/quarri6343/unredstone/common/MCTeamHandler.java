package quarri6343.unredstone.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import quarri6343.unredstone.common.data.URTeam;

import javax.annotation.ParametersAreNonnullByDefault;

public class MCTeamHandler {

    public void addPlayerToMCTeam(Player player, URTeam urTeam) {
        Team team = getBoard().getTeam(urTeam.name);
        if (team == null)
            team = createMinecraftTeam(urTeam);
        team.addPlayer(player);
    }

    /**
     * 新しいminecraftのチームを作る
     */
    private Team createMinecraftTeam(URTeam urTeam) {
        Team team = getBoard().registerNewTeam(urTeam.name);
        team.color(NamedTextColor.NAMES.value(urTeam.color));
        team.displayName(Component.text(urTeam.name).color(NamedTextColor.NAMES.value(urTeam.color)));
        team.setAllowFriendlyFire(false);

        return team;
    }

    /**
     * minecraftのチームを全て解散させる
     */
    public void deleteMinecraftTeams() {
        for (int i = 0; i < getBoard().getTeams().size(); i++) {
            getBoard().getTeams().forEach(team -> team.unregister());
        }
    }

    /**
     * プレイヤーをチームから外す
     *
     * @param player プレイヤー名
     */
    @ParametersAreNonnullByDefault
    public void removePlayerFromMCTeam(Player player) {
        Team team = getBoard().getPlayerTeam(player);
        if (team == null)
            return;

        team.removePlayer(player);
    }

    private Scoreboard getBoard() {
        return Bukkit.getScoreboardManager().getMainScoreboard();
    }
}
