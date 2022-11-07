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

        ItemStack selectTeamItem = new ItemCreator(Material.RESPAWN_ANCHOR).setName(Component.text("設定するチームを変更"))
                .setLore(getSelectedTeamDesc()).create();
        GuiItem selectTeamButton = new GuiItem(selectTeamItem,
                event -> UITeamSelect.openUI(player));
        gui.setItem(1, selectTeamButton);

        GuiItem setStartButton;
        ItemStack setStartItem = new ItemCreator(Material.FURNACE_MINECART).setName(Component.text("チーム" + getData().selectedTeam + "の開始地点を設定"))
                .setLore(getSetStartButtonDesc()).create();
        setStartButton = new GuiItem(setStartItem, UIMenu::onSetStartButton);
        gui.setItem(4, setStartButton);

        GuiItem setEndButton;
        ItemStack setEndItem = new ItemCreator(Material.DETECTOR_RAIL).setName(Component.text("チーム" + getData().selectedTeam + "の終了地点を設定"))
                .setLore(getSetEndButtonDesc()).create();
        setEndButton = new GuiItem(setEndItem, UIMenu::onSetEndButton);
        gui.setItem(7, setEndButton);

        GuiItem startButton = new GuiItem(new ItemCreator(Material.GREEN_WOOL).setName(Component.text("ゲームを開始(未実装につき1チームのみプレイできます)")).setLore(getCanStartGameDesc()).create(),
                event -> {
                    getLogic().startGame((Player) event.getWhoClicked());
                    openUI((Player) event.getWhoClicked());
                });
        gui.setItem(11, startButton);
        GuiItem endButton = new GuiItem(new ItemCreator(Material.RED_WOOL).setName(Component.text("ゲームを強制終了")).setLore(getCanTerminateGameDesc()).create(),
                event -> {
                    getLogic().endGame((Player) event.getWhoClicked(), UnRedstoneLogic.GameResult.FAIL);
                    openUI((Player) event.getWhoClicked());
                });
        gui.setItem(15, endButton);

        GuiItem closeButton = new GuiItem(new ItemCreator(Material.BARRIER).setName(Component.text("閉じる")).create(),
                event -> gui.close(event.getWhoClicked()));
        gui.setItem(22, closeButton);

        gui.open(player);
    }


    /**
     * @return 初期位置を設定するボタンの説明文
     */
    public static TextComponent getSetStartButtonDesc(){
        if(getData().getTeambyName(getData().selectedTeam) == null){
            return teamNotSelectedText;
        }

        if(getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE){
            return gameRunningText;
        }

        return getLocDesc(getData().getTeambyName(getData().selectedTeam).startLocation);
    }
    
    /**
     * @return ゴール位置を設定するボタンの説明文
     */
    public static TextComponent getSetEndButtonDesc(){
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
    public static void onSetStartButton(InventoryClickEvent event){
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
    public static void onSetEndButton(InventoryClickEvent event){
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
     * @param location 文章にしたいLocation
     * @return 渡されたLocationの情報を表す文
     */
    public static TextComponent getLocDesc(Location location) {
        return Component.text(location != null ? "現在：" + UnRedstoneUtils.locationBlockPostoString(location) : "未設定です")
                .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * @return 現在ゲームを開始できるかどうかを示した文
     */
    public static TextComponent getCanStartGameDesc() {
        return getLogic().gameStatus == UnRedstoneLogic.GameStatus.INACTIVE ?
                Component.text("開始可能").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                : Component.text("ゲームが進行中です!").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * @return 現在ゲームを終了できるかどうかを示した文
     */
    public static TextComponent getCanTerminateGameDesc() {
        return getLogic().gameStatus == UnRedstoneLogic.GameStatus.ACTIVE ?
                Component.text("強制終了可能").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                : Component.text("進行中のゲームはありません").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * @return 現在選択中のチームの情報を表す文
     */
    public static TextComponent getSelectedTeamDesc() {
        return Component.text("選択中のチーム:" + UnRedstone.getInstance().data.selectedTeam)
                .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    }
}
