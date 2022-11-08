package quarri6343.unredstone.impl;

import dev.triumphteam.gui.guis.Gui;
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
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.common.UnRedstoneLogic;
import quarri6343.unredstone.utils.ItemCreator;
import quarri6343.unredstone.utils.UnRedstoneUtils;

public class UIMenu {

    private static final TextComponent gameRunningText = Component.text("ゲームが進行中です！").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent teamNotSelectedText = Component.text("チームが選択されていません")
            .color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent setStartButtonGuide = Component.text("現在立っている場所が開始地点になります")
            .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent setEndButtonGuide = Component.text("現在立っている場所が終了地点になります")
            .color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent joinTeamButtonGuide = Component.text("コマンド/forcejoin {プレイヤー名}を使用してください")
            .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    private static final TextComponent leaveTeamButtonGuide = Component.text("コマンド/forceleave {プレイヤー名}を使用してください")
            .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    
    private static UnRedstoneData getData(){
        return UnRedstone.getInstance().data;
    }
    
    private static UnRedstoneLogic getLogic(){
        return UnRedstone.getInstance().logic;
    }
    
    public static void openUI(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("管理メニュー").color(NamedTextColor.GRAY))
                .rows(3)
                .pageSize(27)
                .disableAllInteractions()
                .create();
        
        ItemStack createTeamItem = new ItemCreator(Material.WHITE_BANNER).setName(Component.text("新しいチームを作成"))
                .create();
        GuiItem createTeamButton = new GuiItem(createTeamItem,
                event -> UICreateTeam.openUI(player));
        gui.setItem(0, createTeamButton);

        ItemStack selectTeamItem = new ItemCreator(Material.RESPAWN_ANCHOR).setName(Component.text("設定するチームを選択"))
                .create();
        GuiItem selectTeamButton = new GuiItem(selectTeamItem,
                event -> UISelectTeam.openUI(player));
        gui.setItem(2, selectTeamButton);

        GuiItem setStartButton;
        ItemStack setStartItem = new ItemCreator(Material.FURNACE_MINECART).setName(Component.text("チーム" + getData().selectedTeam + "の開始地点を設定"))
                .addLore(getSetStartButtonStats()).addLore(setStartButtonGuide).create();
        setStartButton = new GuiItem(setStartItem, UIMenu::onSetStartButton);
        gui.setItem(4, setStartButton);

        GuiItem setEndButton;
        ItemStack setEndItem = new ItemCreator(Material.DETECTOR_RAIL).setName(Component.text("チーム" + getData().selectedTeam + "の終了地点を設定"))
                .addLore(getSetEndButtonStats()).addLore(setEndButtonGuide).create();
        setEndButton = new GuiItem(setEndItem, UIMenu::onSetEndButton);
        gui.setItem(6, setEndButton);

        GuiItem removeTeamButton;
        ItemStack removeTeamItem = new ItemCreator(Material.BLACK_BANNER).setName(Component.text("選択中のチームを削除"))
                .setLore(getRemoveTeamDesc()).create();
        removeTeamButton = new GuiItem(removeTeamItem, UIMenu::onRemoveTeamButton);
        gui.setItem(8, removeTeamButton);
        
        ItemStack forceJoinItem = new ItemCreator(Material.GREEN_BANNER).setName(Component.text("選択中のチームにプレイヤーを入れる"))
                .setLore(joinTeamButtonGuide).create();
        GuiItem forceJoinButton = new GuiItem(forceJoinItem, event -> {});
        gui.setItem(11, forceJoinButton);
        ItemStack forceLeaveItem = new ItemCreator(Material.RED_BANNER).setName(Component.text("プレイヤーをチームから外す"))
                .setLore(leaveTeamButtonGuide).create();
        GuiItem forceLeaveButton = new GuiItem(forceLeaveItem, event -> {});
        gui.setItem(15, forceLeaveButton);
        
        GuiItem startButton = new GuiItem(new ItemCreator(Material.GREEN_WOOL).setName(Component.text("ゲームを開始(未実装につき1チームのみプレイできます)")).setLore(getCanStartGameDesc()).create(),
                event -> {
                    getLogic().startGame((Player) event.getWhoClicked());
                    openUI((Player) event.getWhoClicked());
                });
        gui.setItem(20, startButton);
        GuiItem endButton = new GuiItem(new ItemCreator(Material.RED_WOOL).setName(Component.text("ゲームを強制終了")).setLore(getCanTerminateGameDesc()).create(),
                event -> {
                    getLogic().endGame((Player) event.getWhoClicked(), UnRedstoneLogic.GameResult.FAIL);
                    openUI((Player) event.getWhoClicked());
                });
        gui.setItem(24, endButton);

        GuiItem closeButton = new GuiItem(new ItemCreator(Material.BARRIER).setName(Component.text("閉じる")).create(),
                event -> gui.close(event.getWhoClicked()));
        gui.setItem(22, closeButton);
        
        gui.open(player);
    }


    /**
     * @return 初期位置を設定するボタンに表示する現在の状況
     */
    private static TextComponent getSetStartButtonStats(){
        if(getData().getTeambyName(getData().selectedTeam) == null){
            return teamNotSelectedText;
        }

        if(getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE){
            return gameRunningText;
        }

        return getLocDesc(getData().getTeambyName(getData().selectedTeam).startLocation);
    }
    
    /**
     * @return ゴール位置を設定するボタンに表示する現在の状況
     */
    private static TextComponent getSetEndButtonStats(){
        if(getData().getTeambyName(getData().selectedTeam) == null){
            return teamNotSelectedText;
        }

        if(getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE){
            return gameRunningText;
        }

        return getLocDesc(getData().getTeambyName(getData().selectedTeam).endLocation);
    }


    /**
     * 初期位置を設定するボタンを押したときのイベント
     */
    private static void onSetStartButton(InventoryClickEvent event){
        if(getData().getTeambyName(getData().selectedTeam) == null){
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        if (getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE) {
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }

        getData().getTeambyName(getData().selectedTeam).startLocation = event.getWhoClicked().getLocation();
        event.getWhoClicked().sendMessage(Component.text("開始地点を" + UnRedstoneUtils.locationBlockPostoString(event.getWhoClicked().getLocation()) + "に設定しました"));
        openUI((Player) event.getWhoClicked());
    }

    /**
     * 終了位置を設定するボタンを押したときのイベント
     */
    private static void onSetEndButton(InventoryClickEvent event){
        if(getData().getTeambyName(getData().selectedTeam) == null){
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        if (getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE) {
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }

        getData().getTeambyName(getData().selectedTeam).endLocation = event.getWhoClicked().getLocation();
        event.getWhoClicked().sendMessage(Component.text("終了地点を" + UnRedstoneUtils.locationBlockPostoString(event.getWhoClicked().getLocation()) + "に設定しました"));
        openUI((Player) event.getWhoClicked());
    }

    /**
     * チームを削除するボタンを押したときのイベント
     */
    private static void onRemoveTeamButton(InventoryClickEvent event){
        if(getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE){
            event.getWhoClicked().sendMessage(gameRunningText);
            return;
        }
        
        if(getData().selectedTeam.equals("")){
            event.getWhoClicked().sendMessage(teamNotSelectedText);
            return;
        }

        getData().removeTeam(getData().selectedTeam);
        event.getWhoClicked().sendMessage(Component.text("チーム" + getData().selectedTeam + "を削除しました").color(NamedTextColor.WHITE));
        getData().selectedTeam = "";
    }
    
    /**
     * @param location 文章にしたいLocation
     * @return 渡されたLocationの情報を表す文
     */
    private static TextComponent getLocDesc(Location location) {
        return Component.text(location != null ? "現在：" + UnRedstoneUtils.locationBlockPostoString(location) : "未設定です")
                .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * @return 現在ゲームを開始できるかどうかを示した文
     */
    private static TextComponent getCanStartGameDesc() {
        return getLogic().gameStatus == UnRedstoneLogic.GameStatus.INACTIVE ?
                Component.text("開始可能").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                : Component.text("ゲームが進行中です!").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * @return 現在ゲームを終了できるかどうかを示した文
     */
    private static TextComponent getCanTerminateGameDesc() {
        return getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE ?
                Component.text("強制終了可能").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                : Component.text("進行中のゲームはありません").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * @return チーム削除ボタンの説明文
     */
    private static TextComponent getRemoveTeamDesc() {
        if(getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE){
            return gameRunningText;
        }
        
        if(getData().selectedTeam.equals("")){
            return teamNotSelectedText;
        }
        
        return Component.text("選択中のチーム:" + UnRedstone.getInstance().data.selectedTeam)
                .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    }
}
