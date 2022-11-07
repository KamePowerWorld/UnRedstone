package quarri6343.unredstone.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.api.CommandBase;

public class CommandRemoveTeam extends CommandBase {
    private static final String commandName="removeteam";

    public CommandRemoveTeam() {
        super(commandName, 1, 1, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, @Nullable String[] arguments) {
        UnRedstoneData data = UnRedstone.getInstance().data;
        if(data.getTeambyName(arguments[0]) == null){
            sender.sendMessage(Component.text("その名前のチームは存在しません").color(NamedTextColor.RED));
            return true;
        }

        data.removeTeam(arguments[0]);
        sender.sendMessage(Component.text("チーム" + arguments[0] + "を削除しました").color(NamedTextColor.WHITE));
        
        if(data.selectedTeam.equals(arguments[0])){
            data.selectedTeam = "";
        }
        
        return true;
    }
}
