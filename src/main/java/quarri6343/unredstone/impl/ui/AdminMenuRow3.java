package quarri6343.unredstone.impl.ui;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.common.UnRedstoneLogic;
import quarri6343.unredstone.utils.ItemCreator;

public class AdminMenuRow3 {

    private static UnRedstoneData getData() {
        return UnRedstone.getInstance().data;
    }
    private static UnRedstoneLogic getLogic() {
        return UnRedstone.getInstance().logic;
    }

    public static void addElements(PaginatedGui gui, Player player){
        ItemStack setMaxHoldableItemsItem = new ItemCreator(Material.OAK_LOG).setName(Component.text("プレイヤーが所持可能な最大の原木、丸石、線路の数を変更"))
                .setLore(Component.text("現在: " + getData().maxHoldableItems + "個").decoration(TextDecoration.ITALIC, false)).create();
        GuiItem setMaxHoldableItemsButton = new GuiItem(setMaxHoldableItemsItem,
                event -> {
                    UINumberConfiguration.openUI(player, integer -> getData().maxHoldableItems = integer);
                });
        gui.setItem(18, setMaxHoldableItemsButton);

        ItemStack setCraftingCostItem = new ItemCreator(Material.COBBLESTONE).setName(Component.text("線路を一個作るのに必要な材料の数を変更"))
                .setLore(Component.text("現在: " + getData().craftingCost + "個").decoration(TextDecoration.ITALIC, false)).create();
        GuiItem setCraftingCostButton = new GuiItem(setCraftingCostItem,
                event -> {
                    UINumberConfiguration.openUI(player, integer -> getData().craftingCost = integer);
                });
        gui.setItem(20, setCraftingCostButton);

        GuiItem closeButton = new GuiItem(new ItemCreator(Material.BARRIER).setName(Component.text("閉じる")).create(),
                event -> gui.close(event.getWhoClicked()));
        gui.setItem(22, closeButton);

        GuiItem startButton = new GuiItem(new ItemCreator(Material.GREEN_WOOL).setName(Component.text("ゲームを開始")).setLore(getCanStartGameDesc()).create(),
                event -> {
                    getLogic().startGame((Player) event.getWhoClicked());
                    UIAdminMenu.openUI((Player) event.getWhoClicked());
                });
        gui.setItem(24, startButton);
        GuiItem endButton = new GuiItem(new ItemCreator(Material.RED_WOOL).setName(Component.text("ゲームを強制終了")).setLore(getCanTerminateGameDesc()).create(),
                event -> {
                    getLogic().endGame((Player) event.getWhoClicked(), null, UnRedstoneLogic.GameResult.FAIL);
                    UIAdminMenu.openUI((Player) event.getWhoClicked());
                });
        gui.setItem(26, endButton);
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
}
