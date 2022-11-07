package quarri6343.unredstone.impl;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.UnRedstoneData;
import quarri6343.unredstone.utils.ItemCreator;

public class UITeamSelect {

    public static void openUI(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(Component.text("チームセレクタ").color(NamedTextColor.GRAY))
                .rows(3)
                .pageSize(27)
                .disableAllInteractions()
                .create();
        
        UnRedstoneData data = UnRedstone.getInstance().data;
        if(data.getTeamsLength() == 0){
            GuiItem closeButton = new GuiItem(new ItemCreator(Material.BARRIER)
                    .setName(Component.text("まず/create team {チーム名} {チームの色}でチームを作ってください").color(NamedTextColor.WHITE)).create(),
                    event -> {
                        gui.close(event.getWhoClicked());
                    });
            gui.setItem(22, closeButton);
        }
        else{
            for(int i = 0; i < data.getTeamsLength(); i++){
                GuiItem teamSelectButton = new GuiItem(new ItemCreator(Material.WHITE_WOOL)
                        .setName(Component.text("チーム「" + data.getTeam(i).name + "」を選択")).create(),
                        event -> {
                            data.selectedTeam = data.getTeam(event.getSlot()).name;
                            UIMenu.openUI((Player) event.getWhoClicked());
                        });
                gui.setItem(i, teamSelectButton);
            }
        }

        gui.open(player);
    }
}
