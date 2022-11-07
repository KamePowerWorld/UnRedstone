package quarri6343.unredstone.common;

import net.kyori.adventure.text.format.NamedTextColor;
import quarri6343.unredstone.UnRedstone;

import java.util.ArrayList;
import java.util.List;

public class UnRedstoneData {
    public String selectedTeam = "";
    private List<UnRedStoneTeam> teams = new ArrayList<>();
    
    public void addTeam(String name, String color){
        if(name == null || name.equals("")){
            UnRedstone.getInstance().getLogger().severe("無効なチームが登録されました");
            return;
        }

        teams.add(new UnRedStoneTeam(name, color));
    }
    
    public void removeTeam(String name){
        teams.removeIf(team -> team.name.equals(name));
    }
    
    public UnRedStoneTeam getTeam(int index){
        return teams.get(index);
    }
    
    public UnRedStoneTeam getTeambyName(String name){
        return teams.stream().filter(v -> v.name.equals(name)).findFirst().orElse(null);
    }
    
    public UnRedStoneTeam getTeambyColor(NamedTextColor color){
        return teams.stream().filter(v -> v.color.equals(NamedTextColor.NAMES.key(color))).findFirst().orElse(null);
    }

    public UnRedStoneTeam getTeambyColor(String color){
        return teams.stream().filter(v -> v.color.equals(color)).findFirst().orElse(null);
    }
    
    public int getTeamsLength(){
        return teams.size();
    }
    
    public void clearTeam(){
        teams.clear();
    }
}
