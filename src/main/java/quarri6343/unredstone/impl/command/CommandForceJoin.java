package quarri6343.unredstone.impl.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.api.CommandBase;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;

/**
 * プレイヤーを強制的にチームに参加させるコマンド。ゲーム中も実行可能
 */
public class CommandForceJoin extends CommandBase {

    private static final String commandName = "forcejoin";

    public CommandForceJoin() {
        super(commandName, 1, 1, true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable String[] arguments) {
        URData data = UnRedstone.getInstance().data;
        if (data.adminSelectedTeam.isEmpty()) {
            sender.sendMessage("まずGUIで加入させたいチームを選択してください");
            return true;
        }

        Player player = Bukkit.getPlayer(arguments[0]);
        if (player == null) {
            sender.sendMessage("その名前のプレイヤーは存在しません");
            return true;
        }

        if (data.teams.getTeambyPlayer(player) != null) {
            UnRedstone.getInstance().globalTeamHandler.removePlayerFromTeam(player);
            sender.sendMessage(arguments[0] + "が既にチームに入っていたので離脱させました");
        }

        URTeam team = data.teams.getTeambyName(data.adminSelectedTeam);
        if (team == null)
            return true;
        
        UnRedstone.getInstance().globalTeamHandler.addPlayerToTeam(player, team);
        sender.sendMessage(arguments[0] + "をチーム" + data.adminSelectedTeam + "に加入させました");
        return true;
    }
}
