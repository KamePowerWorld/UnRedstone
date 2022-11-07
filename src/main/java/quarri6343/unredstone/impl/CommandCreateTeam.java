package quarri6343.unredstone.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.api.CommandBase;

public class CommandCreateTeam extends CommandBase {

    private static final String commandName="createteam";

    public CommandCreateTeam() {
        super(commandName, 2, 2, true);
    }

    @Override
    public boolean onCommand(CommandSender sender, @Nullable String[] arguments) {
        if(NamedTextColor.NAMES.value(arguments[1]) == null){
            sender.sendMessage(Component.text("チームカラーが不正です。redやgreenのように半角小文字で指定してください").color(NamedTextColor.RED));
            return true;
        }
        
        UnRedstoneData data = UnRedstone.getInstance().data;
        if(data.getTeambyName(arguments[0]) != null){
            sender.sendMessage(Component.text("その名前のチームは既に存在します").color(NamedTextColor.RED));
            return true;
        }
        if(data.getTeambyColor(arguments[1]) != null){
            sender.sendMessage(Component.text("その色のチームは既に存在します").color(NamedTextColor.RED));
            return true;
        }
        
        data.addTeam(arguments[0], arguments[1]);
        sender.sendMessage(Component.text("チーム「" + arguments[0] + "」を作成しました").color(NamedTextColor.NAMES.value(arguments[1])));
        return true;
    }
}
