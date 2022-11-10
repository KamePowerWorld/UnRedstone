package quarri6343.unredstone.impl.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.api.CommandBase;
import quarri6343.unredstone.common.UnRedstoneData;

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
        UnRedstoneData data = UnRedstone.getInstance().data;
        if (data.adminSelectedTeam.equals("")) {
            sender.sendMessage("まずGUIで加入させたいチームを選択してください");
            return true;
        }

        Player player = Bukkit.getPlayer(arguments[0]);
        if (player == null) {
            sender.sendMessage("その名前のプレイヤーは存在しません");
            return true;
        }

        for (int i = 0; i < data.getTeamsLength(); i++) {
            if (data.getTeam(i).players.contains(player)) {
                data.getTeam(i).players.remove(player);
                sender.sendMessage(arguments[0] + "が既にチームに入っていたので離脱させました");
                break;
            }
        }

        data.getTeambyName(data.adminSelectedTeam).players.add(player);
        UnRedstone.getInstance().scoreBoardManager.addPlayerToTeam(player, data.adminSelectedTeam);
        sender.sendMessage(arguments[0] + "をチーム" + data.adminSelectedTeam + "に加入させました");
        return true;
    }
}
