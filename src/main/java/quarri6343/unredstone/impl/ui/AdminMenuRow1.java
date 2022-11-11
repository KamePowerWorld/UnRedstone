package quarri6343.unredstone.impl.ui;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
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

public class AdminMenuRow1 {

    private static final TextComponent setStartButtonGuide = Component.text("現在立っている場所が開始地点になります")
            .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent setEndButtonGuide = Component.text("現在立っている場所が終了地点になります")
            .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

    private static URData getData() {
        return UnRedstone.getInstance().data;
    }

    private static URLogic getLogic() {
        return UnRedstone.getInstance().logic;
    }
    
    public static void addElements(PaginatedGui gui, Player player){
        ItemStack createTeamItem = new ItemCreator(Material.WHITE_BANNER).setName(Component.text("新しいチームを作成"))
                .create();
        GuiItem createTeamButton = new GuiItem(createTeamItem,
                event -> UICreateTeam.openUI(player));
        gui.setItem(0, createTeamButton);

        ItemStack selectTeamItem = new ItemCreator(Material.RESPAWN_ANCHOR).setName(Component.text("設定変更するチームを選択"))
                .create();
        GuiItem selectTeamButton = new GuiItem(selectTeamItem,
                event -> UIAdminSelectTeam.openUI(player));
        gui.setItem(2, selectTeamButton);

        GuiItem setStartButton;
        ItemStack setStartItem = new ItemCreator(Material.FURNACE_MINECART).setName(Component.text("チーム" + getData().adminSelectedTeam + "のゲーム開始地点を設定"))
                .addLore(getSetStartButtonStats()).addLore(setStartButtonGuide).create();
        setStartButton = new GuiItem(setStartItem,
                event -> {
                    onSetStartButton(event);
                    UIAdminMenu.openUI((Player) event.getWhoClicked());
                });
        gui.setItem(4, setStartButton);

        GuiItem setEndButton;
        ItemStack setEndItem = new ItemCreator(Material.DETECTOR_RAIL).setName(Component.text("チーム" + getData().adminSelectedTeam + "のゲーム終了地点を設定"))
                .addLore(getSetEndButtonStats()).addLore(setEndButtonGuide).create();
        setEndButton = new GuiItem(setEndItem,
                event -> {
                    onSetEndButton(event);
                    UIAdminMenu.openUI((Player) event.getWhoClicked());
                });
        gui.setItem(6, setEndButton);

        GuiItem removeTeamButton;
        ItemStack removeTeamItem = new ItemCreator(Material.BLACK_BANNER).setName(Component.text("選択中のチームを削除"))
                .setLore(getRemoveTeamDesc()).create();
        removeTeamButton = new GuiItem(removeTeamItem, AdminMenuRow1::onRemoveTeamButton);
        gui.setItem(8, removeTeamButton);
    }
    
    /**
     * @return 初期位置を設定するボタンに表示する現在の状況
     */
    private static TextComponent getSetStartButtonStats() {
        URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
        if (team == null) {
            return teamNotSelectedText;
        }

        if (getLogic().gameStatus == URLogic.GameStatus.ACTIVE) {
            return gameRunningText;
        }

        return getLocDesc(team.getStartLocation());
    }

    /**
     * @return ゴール位置を設定するボタンに表示する現在の状況
     */
    private static TextComponent getSetEndButtonStats() {
        URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
        if (team == null) {
            return teamNotSelectedText;
        }

        if (getLogic().gameStatus == URLogic.GameStatus.ACTIVE) {
            return gameRunningText;
        }

        return getLocDesc(team.getEndLocation());
    }

    /**
     * 初期位置を設定するボタンを押したときのイベント
     */
    private static void onSetStartButton(InventoryClickEvent event) {
        URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
        if (team == null) {
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        if (getLogic().gameStatus == URLogic.GameStatus.ACTIVE) {
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }

        Location startLocation = event.getWhoClicked().getLocation();
        if(!team.isStartLocationValid(startLocation)){
            event.getWhoClicked().sendMessage(Component.text("終了位置と近すぎます"));
            return;
        }
        
        team.setStartLocation(startLocation);
        event.getWhoClicked().sendMessage(Component.text("開始地点を" + UIUtility.locationBlockPostoString(startLocation) + "に設定しました"));
        UIAdminMenu.openUI((Player) event.getWhoClicked());
    }

    /**
     * 終了位置を設定するボタンを押したときのイベント
     */
    private static void onSetEndButton(InventoryClickEvent event) {
        URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
        if (team == null) {
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        if (getLogic().gameStatus == URLogic.GameStatus.ACTIVE) {
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }

        Location endLocation = event.getWhoClicked().getLocation();
        if(!team.isEndLocationValid(endLocation)){
            event.getWhoClicked().sendMessage(Component.text("開始位置と近すぎます"));
            return;
        }

        team.setEndLocation(endLocation);
        event.getWhoClicked().sendMessage(Component.text("終了地点を" + UIUtility.locationBlockPostoString(endLocation) + "に設定しました"));
        UIAdminMenu.openUI((Player) event.getWhoClicked());
    }

    /**
     * チームを削除するボタンを押したときのイベント
     */
    private static void onRemoveTeamButton(InventoryClickEvent event) {
        if (getLogic().gameStatus == URLogic.GameStatus.ACTIVE) {
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }

        if (getData().adminSelectedTeam.isEmpty()) {
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
        if(team == null)
            return;

        for (Player player : team.players)
            UnRedstone.getInstance().scoreBoardManager.kickPlayerFromMCTeam(player);
        getData().teams.removeTeam(getData().adminSelectedTeam);
        event.getWhoClicked().sendMessage(Component.text("チーム" + getData().adminSelectedTeam + "を削除しました").color(NamedTextColor.WHITE));
        getData().adminSelectedTeam = "";
    }

    /**
     * @return チーム削除ボタンの説明文
     */
    private static TextComponent getRemoveTeamDesc() {
        if (getLogic().gameStatus == URLogic.GameStatus.ACTIVE) {
            return gameRunningText;
        }

        if (getData().adminSelectedTeam.equals("")) {
            return teamNotSelectedText;
        }

        return Component.text("選択中のチーム:" + UnRedstone.getInstance().data.adminSelectedTeam)
                .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    }
}
