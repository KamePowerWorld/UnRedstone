package quarri6343.unredstone.impl.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.api.CommandBase;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.impl.ui.AdminMenuRow3;

import java.util.Objects;

/**
 * ビーコンを設置するコマンド
 */
public class CommandPlaceBeacon extends CommandBase {

    private static final String commandName = "unredstone_place_beacon";

    public CommandPlaceBeacon() {
        super(commandName, 1, 1, false);
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

        // チームのスタート地点を設定
        AdminMenuRow3.onPlaceBeaconButton(sender, team);
        sender.sendMessage(teamText + " チームのビーコンを設置しました！");
        return true;
    }

    private static URData getData() {
        return UnRedstone.getInstance().getData();
    }
}
