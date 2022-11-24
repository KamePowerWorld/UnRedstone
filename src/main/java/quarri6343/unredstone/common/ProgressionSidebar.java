package quarri6343.unredstone.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;

/**
 * ゲームの進捗を表示するサイドバーを管理する
 */
public class ProgressionSidebar {
    
    private static final String objectiveName = "progression";
    private static Objective objective;
    
    public static void initialize(){
        objective = getBoard().getObjective(objectiveName);
        if(objective == null)
            objective = getBoard().registerNewObjective(objectiveName, "dummy", ChatColor.RED + "ゴールまでの距離");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        update();
    }
    
    public static void update(){
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            if(team.getEndLocation() == null || team.locomotive == null)
                continue;
            
            Score score = objective.getScore(team.name);
            int distance = (int)team.getEndLocation().distance(team.locomotive.entity.getLocation());
            score.setScore(distance);
        }
    }
    
    public static void destroy(){
        if(objective == null)
            return;
        
        objective.unregister();
    }

    private static Scoreboard getBoard() {
        return Bukkit.getScoreboardManager().getMainScoreboard();
    }

    private static URData getData() {
        return UnRedstone.getInstance().getData();
    }
}
