package quarri6343.unredstone.impl;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.UnRedstoneLogic;
import quarri6343.unredstone.utils.ItemCreator;
import quarri6343.unredstone.utils.UnRedstoneUtils;

public class UIMenu {

    public static void openUI(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("管理メニュー"))
                .rows(3)
                .pageSize(27)
                .disableAllInteractions()
                .create();
        
        GuiItem setStartButton = new GuiItem(new ItemCreator(Material.FURNACE_MINECART).setName(Component.text("開始地点を設定")).create(),
                event -> {
                    UnRedstone.getInstance().config.data.startLocation = event.getWhoClicked().getLocation();
                    event.getWhoClicked().sendMessage(Component.text("開始地点を" + UnRedstoneUtils.locationBlockPostoString(event.getWhoClicked().getLocation()) + "に設定しました"));
                });
        gui.setItem(1, setStartButton);
        GuiItem setRelay1Button = new GuiItem(new ItemCreator(Material.BEACON).setName(Component.text("中継地点1を設定")).create(),
                event -> {
                    UnRedstone.getInstance().config.data.relayLocation1 = event.getWhoClicked().getLocation();
                    event.getWhoClicked().sendMessage(Component.text("中継地点1を" + UnRedstoneUtils.locationBlockPostoString(event.getWhoClicked().getLocation()) + "に設定しました"));
                });
        gui.setItem(3, setRelay1Button);
        GuiItem setRelay2Button = new GuiItem(new ItemCreator(Material.BEACON).setName(Component.text("中継地点2を設定")).create(),
                event -> {
                    UnRedstone.getInstance().config.data.relayLocation2 = event.getWhoClicked().getLocation();
                    event.getWhoClicked().sendMessage(Component.text("中継地点2を" + UnRedstoneUtils.locationBlockPostoString(event.getWhoClicked().getLocation()) + "に設定しました"));
                });
        gui.setItem(5, setRelay2Button);
        GuiItem setEndButton = new GuiItem(new ItemCreator(Material.DETECTOR_RAIL).setName(Component.text("終了地点を設定")).create(),
                event -> {
                    UnRedstone.getInstance().config.data.endLocation = event.getWhoClicked().getLocation();
                    event.getWhoClicked().sendMessage(Component.text("終了地点を" + UnRedstoneUtils.locationBlockPostoString(event.getWhoClicked().getLocation()) + "に設定しました"));
                });
        gui.setItem(7, setEndButton);

        GuiItem startButton = new GuiItem(new ItemCreator(Material.GREEN_WOOL).setName(Component.text("ゲームを開始")).create(),
                event -> {
                    UnRedstone.getInstance().logic.startGame((Player)event.getWhoClicked());
                });
        gui.setItem(11, startButton);
        GuiItem endButton = new GuiItem(new ItemCreator(Material.RED_WOOL).setName(Component.text("ゲームを強制終了")).create(),
                event -> {
                    UnRedstone.getInstance().logic.endGame((Player)event.getWhoClicked(), UnRedstoneLogic.GameResult.FAIL);
                });
        gui.setItem(15, endButton);

        GuiItem closeButton = new GuiItem(Material.BARRIER,
                event -> {
                    gui.close(event.getWhoClicked());
                });
        gui.setItem(22, closeButton);

        gui.open(player);
    }
}
