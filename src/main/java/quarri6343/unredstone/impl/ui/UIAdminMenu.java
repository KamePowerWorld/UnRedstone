package quarri6343.unredstone.impl.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * プラグインの管理パネル
 */
public class UIAdminMenu {

    public static void openUI(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("管理メニュー").color(NamedTextColor.GRAY))
                .rows(3)
                .pageSize(27)
                .disableAllInteractions()
                .create();
        
        AdminMenuRow1.addElements(gui, player);
        AdminMenuRow2.addElements(gui, player);
        AdminMenuRow3.addElements(gui, player);

        gui.open(player);
    }
}
