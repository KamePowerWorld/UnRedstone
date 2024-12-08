package quarri6343.unredstone.impl.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.api.CommandBase;
import quarri6343.unredstone.common.logic.URLogic;

/**
 * ゲームを終了するコマンド
 */
public class CommandEnd extends CommandBase {

    private static final String commandName = "unredstone_end";

    public CommandEnd() {
        super(commandName, 0, 1, false);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @Nullable String[] arguments) {
        getLogic().endGame();
        sender.sendMessage("ゲームを強制終了します！");
        return true;
    }

    private static URLogic getLogic() {
        return UnRedstone.getInstance().getLogic();
    }
}
