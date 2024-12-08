package quarri6343.unredstone.impl.ui;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.GlobalTeamHandler;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.utils.ItemCreator;
import quarri6343.unredstone.utils.UIUtility;

import static quarri6343.unredstone.utils.UIUtility.*;

public class AdminMenuRow1 {

    private static final TextComponent setStartButtonGuide = Component.text("現在立っている場所が開始地点になります")
            .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent setEndButtonGuide = Component.text("現在立っている場所が終了地点になります")
            .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);

    private static URData getData() {
        return UnRedstone.getInstance().getData();
    }

    private static URLogic getLogic() {
        return UnRedstone.getInstance().getLogic();
    }

    public static void addElements(PaginatedGui gui, Player player) {
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
                    Player clickedPlayer = (Player) event.getWhoClicked();

                    URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
                    if (team == null) {
                        clickedPlayer.sendMessage(teamNotSelectedText);
                        return;
                    }

                    if (onSetStartButton(clickedPlayer, team, clickedPlayer.getLocation())) {
                        UIAdminMenu.openUI(clickedPlayer);
                    }
                });
        gui.setItem(4, setStartButton);

        GuiItem setEndButton;
        ItemStack setEndItem = new ItemCreator(Material.DETECTOR_RAIL).setName(Component.text("チーム" + getData().adminSelectedTeam + "のゲーム終了地点を設定"))
                .addLore(getSetEndButtonStats()).addLore(setEndButtonGuide).create();
        setEndButton = new GuiItem(setEndItem,
                event -> {
                    Player clickedPlayer = (Player) event.getWhoClicked();

                    URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
                    if (team == null) {
                        clickedPlayer.sendMessage(teamNotSelectedText);
                        return;
                    }

                    if (onSetEndButton(clickedPlayer, team, clickedPlayer.getLocation())) {
                        UIAdminMenu.openUI(clickedPlayer);
                    }
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

        return getLocDesc(team.getEndLocation());
    }

    /**
     * 初期位置を設定するボタンを押したときのイベント
     */
    public static boolean onSetStartButton(CommandSender sender, URTeam team, Location location) {
        if (getLogic().gameStatus != URLogic.GameStatus.INACTIVE) {
            sender.sendMessage(gameRunningText);
            return false;
        }

        if (!team.isStartLocationValid(location)) {
            sender.sendMessage(Component.text("終了位置と近すぎます"));
            return false;
        }

        team.setStartLocation(location);
        sender.sendMessage(Component.text("開始地点を" + UIUtility.locationBlockPostoString(location) + "に設定しました"));
        return true;
    }

    /**
     * 終了位置を設定するボタンを押したときのイベント
     */
    public static boolean onSetEndButton(CommandSender sender, URTeam team, Location location) {
        if (getLogic().gameStatus != URLogic.GameStatus.INACTIVE) {
            sender.sendMessage(gameRunningText);
            return false;
        }

        if (!team.isEndLocationValid(location)) {
            sender.sendMessage(Component.text("開始位置と近すぎます"));
            return false;
        }

        team.setEndLocation(location);
        sender.sendMessage(Component.text("終了地点を" + UIUtility.locationBlockPostoString(location) + "に設定しました"));
        return true;
    }

    /**
     * チームを削除するボタンを押したときのイベント
     */
    public static void onRemoveTeamButton(InventoryClickEvent event) {
        if (getLogic().gameStatus != URLogic.GameStatus.INACTIVE) {
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }

        if (getData().adminSelectedTeam.isEmpty()) {
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        URTeam team = getData().teams.getTeambyName(getData().adminSelectedTeam);
        if (team == null)
            return;

        for (int i = 0; i < team.getPlayersSize(); i++) {
            GlobalTeamHandler.removePlayerFromTeam(team.getPlayer(i), false);
        }
        getData().teams.removeTeam(getData().adminSelectedTeam);
        event.getWhoClicked().sendMessage(Component.text("チーム" + getData().adminSelectedTeam + "を削除しました").color(NamedTextColor.WHITE));
        getData().adminSelectedTeam = "";
    }

    /**
     * @return チーム削除ボタンの説明文
     */
    private static TextComponent getRemoveTeamDesc() {
        if (getLogic().gameStatus != URLogic.GameStatus.INACTIVE) {
            return gameRunningText;
        }

        if (getData().adminSelectedTeam.equals("")) {
            return teamNotSelectedText;
        }

        return Component.text("選択中のチーム:" + getData().adminSelectedTeam)
                .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    }
}
