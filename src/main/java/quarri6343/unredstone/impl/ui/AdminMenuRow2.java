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
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.common.UnRedstoneLogic;
import quarri6343.unredstone.common.UnRedstoneTeam;
import quarri6343.unredstone.utils.ItemCreator;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import static quarri6343.unredstone.utils.UIUtility.*;
import static quarri6343.unredstone.utils.UIUtility.teamNotSelectedText;

public class AdminMenuRow2 {
    private static final TextComponent joinTeamButtonGuide = Component.text("コマンド/forcejoin {プレイヤー名}を使用してください")
            .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent leaveTeamButtonGuide = Component.text("コマンド/forceleave {プレイヤー名}を使用してください")
            .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent setJoinLocationButtonGuide = Component.text("ゲームが始まった時このエリア内にいる人は選択中のチームに参加できます")
            .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

    private static UnRedstoneData getData() {
        return UnRedstone.getInstance().data;
    }
    
    private static UnRedstoneLogic getLogic() {
        return UnRedstone.getInstance().logic;
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

        ItemStack setJoinLocation1Item = new ItemCreator(Material.STRUCTURE_BLOCK).setName(Component.text("チーム" + getData().adminSelectedTeam + "の参加エリアの始点を選ぶ"))
                .addLore(getSetJoinLocation1ButtonStats()).addLore(setJoinLocationButtonGuide).create();
        GuiItem setJoinLocation1Button = new GuiItem(setJoinLocation1Item,
                event -> {
                    onSetJoinLocationButton(event, true);
                    UIAdminMenu.openUI((Player) event.getWhoClicked());
                });
        gui.setItem(13, setJoinLocation1Button);

        ItemStack setJoinLocation2Item = new ItemCreator(Material.STRUCTURE_BLOCK).setName(Component.text("チーム" + getData().adminSelectedTeam + "の参加エリアの終点を選ぶ"))
                .setLore(getSetJoinLocation2ButtonStats()).addLore(setJoinLocationButtonGuide).create();
        GuiItem setJoinLocation2Button = new GuiItem(setJoinLocation2Item,
                event -> {
                    onSetJoinLocationButton(event, false);
                    UIAdminMenu.openUI((Player) event.getWhoClicked());
                });
        gui.setItem(15, setJoinLocation2Button);

        ItemStack resetTeamSettingsItem = new ItemCreator(Material.PUFFERFISH).setName(Component.text("チーム" + getData().adminSelectedTeam + "の設定をリセットする")).create();
        GuiItem resetTeamSettingsButton = new GuiItem(resetTeamSettingsItem,
                event -> {
                    onResetTeamSettingsButton(event);
                    UIAdminMenu.openUI((Player) event.getWhoClicked());
                });
        gui.setItem(17, resetTeamSettingsButton);
    }

    private static TextComponent getSetJoinLocation1ButtonStats() {
        if (getData().getTeambyName(getData().adminSelectedTeam) == null) {
            return teamNotSelectedText;
        }

        return getLocDesc(getData().getTeambyName(getData().adminSelectedTeam).joinLocation1);
    }

    private static TextComponent getSetJoinLocation2ButtonStats() {
        if (getData().getTeambyName(getData().adminSelectedTeam) == null) {
            return teamNotSelectedText;
        }

        return getLocDesc(getData().getTeambyName(getData().adminSelectedTeam).joinLocation2);
    }

    private static void onResetTeamSettingsButton(InventoryClickEvent event) {
        if (getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE) {
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }

        UnRedstoneTeam team = getData().getTeambyName(getData().adminSelectedTeam);
        if (team == null) {
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        team.startLocation = null;
        team.endLocation = null;
        team.joinLocation1 = null;
        team.joinLocation2 = null;
        event.getWhoClicked().sendMessage(Component.text("チーム" + team.name + "の設定をリセットしました"));
    }

    private static void onSetJoinLocationButton(InventoryClickEvent event, boolean isLocation1) {
        if (getData().getTeambyName(getData().adminSelectedTeam) == null) {
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        if (isLocation1) {
            getData().getTeambyName(getData().adminSelectedTeam).joinLocation1 = event.getWhoClicked().getLocation();
            event.getWhoClicked().sendMessage(Component.text("チーム" + getData().adminSelectedTeam + "の参加エリアの始点を" + UnRedstoneUtils.locationBlockPostoString(event.getWhoClicked().getLocation()) + "に設定しました"));
        } else {
            getData().getTeambyName(getData().adminSelectedTeam).joinLocation2 = event.getWhoClicked().getLocation();
            event.getWhoClicked().sendMessage(Component.text("チーム" + getData().adminSelectedTeam + "の参加エリアの終点を" + UnRedstoneUtils.locationBlockPostoString(event.getWhoClicked().getLocation()) + "に設定しました"));
        }
    }
}
