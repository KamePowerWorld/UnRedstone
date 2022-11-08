package quarri6343.unredstone.impl.ui;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.UnRedStoneTeam;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.common.UnRedstoneLogic;
import quarri6343.unredstone.utils.ItemCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * プレイヤーがチームに参加できるGUI
 */
public class UIJoinTeam {
    
    public static void openUI(Player player) {
        
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("チーム参加画面").color(NamedTextColor.GRAY))
                .rows(3)
                .pageSize(27)
                .disableAllInteractions()
                .create();

        UnRedstoneData data = UnRedstone.getInstance().data;
        if (data.getTeamsLength() == 0) {
            GuiItem closeButton = new GuiItem(new ItemCreator(Material.BARRIER)
                    .setName(Component.text("加入可能なチームが存在しません！").color(NamedTextColor.WHITE)).create(),
                    event -> {
                        gui.close(event.getWhoClicked());
                    });
            gui.setItem(22, closeButton);
        } else {
            for (int i = 0; i < data.getTeamsLength(); i++) {
                List<Component> lores = new ArrayList<>();
                lores.add(Component.text("プレイヤー数: " + data.getTeam(i).players.size()));
                lores.addAll(data.getTeam(i).players.stream().map(
                        player1 -> Component.text(player1.getName()).color(NamedTextColor.GRAY)).toList());

                ItemStack teamSelectItem = new ItemCreator(Material.WHITE_WOOL)
                        .setName(Component.text("チーム" + data.getTeam(i).name + "に加入"))
                        .setLores(lores).create();
                GuiItem teamSelectButton = new GuiItem(teamSelectItem,
                        event -> {
                            onTeamSelected(event, data.getTeam(event.getSlot()));
                            gui.close(event.getWhoClicked());
                        });
                gui.setItem(i, teamSelectButton);
            }
        }

        gui.open(player);
    }

    /**
     * プレイヤーが参加したいチームを選択した時の挙動
     */
    private static void onTeamSelected(InventoryClickEvent event, UnRedStoneTeam team) {
        if(UnRedstone.getInstance().logic.gameStatus == UnRedstoneLogic.GameStatus.ACTIVE){
            event.getWhoClicked().sendMessage(Component.text("ゲーム中はチームに加入できません").color(NamedTextColor.RED));
            return;
        }
        
        UnRedstoneData data = UnRedstone.getInstance().data;

        for (int i = 0; i < data.getTeamsLength(); i++) {
            if (data.getTeam(i).players.contains((Player) event.getWhoClicked())) {
                data.getTeam(i).players.remove((Player) event.getWhoClicked());
                break;
            }
        }

        team.players.add((Player) event.getWhoClicked());
        event.getWhoClicked().sendMessage("チーム" + team.name + "に加入しました");
    }
}
