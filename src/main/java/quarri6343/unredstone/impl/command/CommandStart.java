package quarri6343.unredstone.impl.command;

import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.api.CommandBase;
import quarri6343.unredstone.common.logic.URLogic;

/**
 * ゲームを開始するコマンド
 */
public class CommandStart extends CommandBase {

    private static final String commandName = "unredstone_start";

    public CommandStart() {
        super(commandName, 0, 1, false);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable String[] arguments) {
        World targetWorld;
        if (sender instanceof Entity) {
            targetWorld = ((Entity) sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            targetWorld = ((BlockCommandSender) sender).getBlock().getWorld();
        } else {
            sender.sendMessage("プレイヤー名を指定してください！");
            return false;
        }

        getLogic().startGame(sender, targetWorld);
        sender.sendMessage("ゲームを開始します！");
        return true;
    }

    private static URLogic getLogic() {
        return UnRedstone.getInstance().getLogic();
    }
}
