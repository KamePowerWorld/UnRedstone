package quarri6343.unredstone.common;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;

import javax.annotation.ParametersAreNonnullByDefault;

public class ScoreBoardHandler {

    /**
     * データに存在するチームからminecraftのチームを作る
     */
    public void createMinecraftTeam() {

        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam urTeam = getData().teams.getTeam(i);
            if (urTeam.players.size() == 0)
                continue;

            Team team = getBoard().getTeam(urTeam.name);
            if(team == null)
                team = getBoard().registerNewTeam(urTeam.name);
            
            team.color(NamedTextColor.NAMES.value(urTeam.color));
            team.setPrefix(ChatColor.RED.toString());
            team.setSuffix(ChatColor.RESET.toString());
            team.setDisplayName(urTeam.name);
            team.setAllowFriendlyFire(false);

            for (Player player : urTeam.players) {
                team.addPlayer(player);
            }
        }
    }

    /**
     * minecraftのチームを全て解散させる
     */
    public void deleteMinecraftTeam() {
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            Team team = getBoard().getTeam(getData().teams.getTeam(i).name);
            if (team != null)
                team.unregister();
        }
    }

    /**
     * プレイヤーを既存のチームに入れる
     * @param player プレイヤー名
     * @param teamName チーム名
     */
    @ParametersAreNonnullByDefault
    public void addPlayerToMCTeam(Player player, String teamName){
        Team team = getBoard().getTeam(teamName);
        if(team == null)
            return;
        
        team.addPlayer(player);
    }

    /**
     * プレイヤーをチームから外す
     * @param player プレイヤー名
     */
    @ParametersAreNonnullByDefault
    public void kickPlayerFromMCTeam(Player player){
        Team team = getBoard().getPlayerTeam(player);
        if(team == null)
            return;
        
        team.removePlayer(player);
    }

    private URData getData() {
        return UnRedstone.getInstance().data;
    }
    
    private Scoreboard getBoard(){
        return Bukkit.getScoreboardManager().getMainScoreboard();
    }
}
