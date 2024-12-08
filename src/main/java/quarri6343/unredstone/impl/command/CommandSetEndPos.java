package quarri6343.unredstone.impl.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.api.CommandBase;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.impl.ui.AdminMenuRow1;

import java.util.Objects;

/**
 * ゴール地点を設定するコマンド
 */
public class CommandSetEndPos extends CommandBase {

    private static final String commandName = "unredstone_set_end_pos";

    public CommandSetEndPos() {
        super(commandName, 4, 4, false);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable String[] arguments) {
        // チーム名を取得
        String teamText = Objects.requireNonNull(arguments[0]);
        URTeam team = getData().teams.getTeambyName(teamText);
        if (team == null) {
            sender.sendMessage("チームが存在しません！");
            return false;
        }

        // 座標
        int x, y, z;
        try {
            x = Integer.parseInt(Objects.requireNonNull(arguments[1]));
            y = Integer.parseInt(Objects.requireNonNull(arguments[2]));
            z = Integer.parseInt(Objects.requireNonNull(arguments[3]));
        } catch (NumberFormatException e) {
            sender.sendMessage("数字を入力してください！");
            sender.sendMessage("/unredstone_set_end_pos <team> <x> <y> <z>");
            return false;
        }

        // ワールド
        World targetWorld;
        if (sender instanceof Entity) {
            targetWorld = ((Entity) sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            targetWorld = ((BlockCommandSender) sender).getBlock().getWorld();
        } else {
            sender.sendMessage("プレイヤー、またはコマンドブロックから打ってください！");
            return false;
        }

        // チームのゴール地点を設定
        AdminMenuRow1.onSetEndButton(sender, team, new Location(targetWorld, x, y, z));
        sender.sendMessage(teamText + " チームのゴール地点を設定しました！");
        return true;
    }

    private static URLogic getLogic() {
        return UnRedstone.getInstance().getLogic();
    }

    private static URData getData() {
        return UnRedstone.getInstance().getData();
    }
}
