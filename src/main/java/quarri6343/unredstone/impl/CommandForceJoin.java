package quarri6343.unredstone.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.api.CommandBase;
import quarri6343.unredstone.common.EventHandler;
import quarri6343.unredstone.common.UnRedStoneTeam;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.utils.ItemCreator;

public class CommandForceJoin extends CommandBase {
    
    private static final String commandName="forcejoin";
    
    public CommandForceJoin() {
        super(commandName, 1, 1, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, @Nullable String[] arguments) {
        UnRedstoneData data = UnRedstone.getInstance().data;
        if(data.selectedTeam.equals("")){
            sender.sendMessage("まずGUIで加入させたいチームを選択してください");
            return true;
        }
        
        Player player = Bukkit.getPlayer(arguments[0]);
        if(player == null){
            sender.sendMessage("その名前のプレイヤーは存在しません");
            return true;
        }
        
        for (int i = 0; i < data.getTeamsLength(); i++) {
            if(data.getTeam(i).players.contains(player)){
                data.getTeam(i).players.remove(player);
                sender.sendMessage(arguments[0] + "が既にチームに入っていたので離脱させました");
                break;
            }
        }
        
        data.getTeambyName(data.selectedTeam).players.add(player);
        sender.sendMessage(arguments[0] + "をチーム" + data.selectedTeam + "に加入させました");
        return true;
    }
}
