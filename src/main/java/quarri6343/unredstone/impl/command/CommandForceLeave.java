package quarri6343.unredstone.impl.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.api.CommandBase;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.common.UnRedstoneTeam;

/**
 * プレイヤーを強制的にチームから外すコマンド。ゲーム中も実行可能
 */
public class CommandForceLeave extends CommandBase {
    
    private static final String commandName="forceleave";
    
    public CommandForceLeave() {
        super(commandName, 1, 1, true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable String[] arguments) {
        Player player = Bukkit.getPlayer(arguments[0]);
        if(player == null){
            sender.sendMessage("その名前のプレイヤーは存在しません");
            return true;
        }

        UnRedstoneData data = UnRedstone.getInstance().data;
        UnRedstoneTeam team = data.getTeambyPlayer(player);
        if(team == null){
            sender.sendMessage(Component.text("プレイヤー" + arguments[0] + "はチームに所属していません").color(NamedTextColor.RED));
            return true;
        }

        team.players.remove(player);
        sender.sendMessage(arguments[0] + "をチーム" + team.name + "から離脱させました");
        return true;
    }
}
