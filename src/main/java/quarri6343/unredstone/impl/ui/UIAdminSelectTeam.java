package quarri6343.unredstone.impl.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.utils.ItemCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理者が設定したいチームを選択する用のGUI
 */
public class UIAdminSelectTeam {

    public static void openUI(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("設定用チームセレクタ").color(NamedTextColor.GRAY))
                .rows(3)
                .pageSize(27)
                .disableAllInteractions()
                .create();

        UnRedstoneData data = UnRedstone.getInstance().data;
        if (data.getTeamsLength() == 0) {
            GuiItem closeButton = new GuiItem(new ItemCreator(Material.BARRIER)
                    .setName(Component.text("まず/create team {チーム名} {チームの色}でチームを作ってください").color(NamedTextColor.WHITE)).create(),
                    event -> gui.close(event.getWhoClicked()));
            gui.setItem(22, closeButton);
        } else {
            for (int i = 0; i < data.getTeamsLength(); i++) {
                List<Component> lores = new ArrayList<>();
                lores.add(Component.text("プレイヤー数: " + data.getTeam(i).players.size()));
                lores.addAll(data.getTeam(i).players.stream().map(
                        player1 -> Component.text(player1.getName()).color(NamedTextColor.GRAY)).toList());

                ItemStack teamSelectItem = new ItemCreator(Material.WHITE_WOOL)
                        .setName(Component.text("チーム" + data.getTeam(i).name + "を選択"))
                        .setLores(lores).create();
                GuiItem teamSelectButton = new GuiItem(teamSelectItem,
                        event -> {
                            data.adminSelectedTeam = data.getTeam(event.getSlot()).name;
                            event.getWhoClicked().sendMessage("チーム" + data.adminSelectedTeam + "を選択しました");
                            UIAdminMenu.openUI((Player) event.getWhoClicked());
                        });
                gui.setItem(i, teamSelectButton);
            }
        }

        gui.open(player);
    }
}
