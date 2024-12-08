package quarri6343.unredstone.impl.ui;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.utils.ItemCreator;
import quarri6343.unredstone.utils.UIUtility;

import static quarri6343.unredstone.utils.UIUtility.*;
import static quarri6343.unredstone.utils.UIUtility.teamNotSelectedText;

public class AdminMenuRow2 {
    private static final TextComponent joinTeamButtonGuide = Component.text("コマンド/forcejoin {プレイヤー名}を使用してください")
            .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent leaveTeamButtonGuide = Component.text("コマンド/forceleave {プレイヤー名}を使用してください")
            .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent setJoinLocationButtonGuide = Component.text("ゲームが始まった時このエリア内にいる人は選択中のチームに参加できます")
            .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

    private static URData getData() {
        return UnRedstone.getInstance().getData();
    }
    
    private static URLogic getLogic() {
        return UnRedstone.getInstance().getLogic();
    }
    
    public static void addElements(PaginatedGui gui, Player player){
        ItemStack forceJoinItem = new ItemCreator(Material.GREEN_BANNER).setName(Component.text("選択中のチームにプレイヤーを強制加入させる"))
                .setLore(joinTeamButtonGuide).create();
        GuiItem forceJoinButton = new GuiItem(forceJoinItem, event -> {
        });
        gui.setItem(9, forceJoinButton);

        ItemStack forceLeaveItem = new ItemCreator(Material.RED_BANNER).setName(Component.text("プレイヤーをチームから外す"))
                .setLore(leaveTeamButtonGuide).create();
        GuiItem forceLeaveButton = new GuiItem(forceLeaveItem, event -> {
        });
        gui.setItem(11, forceLeaveButton);

        ItemStack resetTeamSettingsItem = new ItemCreator(Material.PUFFERFISH).setName(Component.text("チーム" + getData().adminSelectedTeam + "の設定をリセットする")).create();
        GuiItem resetTeamSettingsButton = new GuiItem(resetTeamSettingsItem,
                event -> {
                    onResetTeamSettingsButton(event);
                    UIAdminMenu.openUI((Player) event.getWhoClicked());
                });
        gui.setItem(17, resetTeamSettingsButton);
    }

    /**
     * チーム設定をリセットするボタンの挙動
     * @param event
     */
    private static void onResetTeamSettingsButton(InventoryClickEvent event) {
        if (getLogic().gameStatus != URLogic.GameStatus.INACTIVE) {
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }

        URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
        if (team == null) {
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        team.setStartLocation(null);
        team.setEndLocation(null);
        event.getWhoClicked().sendMessage(Component.text("チーム" + team.name + "の設定をリセットしました"));
    }
}
