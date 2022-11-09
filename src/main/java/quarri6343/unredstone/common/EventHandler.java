package quarri6343.unredstone.common;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.impl.ui.UIAdminMenu;

public class EventHandler implements Listener {
    
    public static final String menuItemName = "UnRedstone管理メニュー";
    
    public EventHandler(){
        UnRedstone.getInstance().getServer().getPluginManager().registerEvents(this, UnRedstone.getInstance());
    }
    
    @org.bukkit.event.EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        processHandheldItem(event);
    }

    /**
     * 手持ちのアイテムを識別してそれに対応したguiを開く
     * @param event
     */
    private void processHandheldItem(PlayerInteractEvent event){
        if(event.getItem() != null){
            if(event.getItem().getType().equals(Material.STICK)
                    && event.getItem().getItemMeta().getDisplayName().equals(menuItemName) && event.getPlayer().isOp()){
                UIAdminMenu.openUI(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }
    
    @org.bukkit.event.EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event){
        processLocomotiveDestruction(event);
    }

    /**
     * トロッコが破壊された場合、チームの初期地点に位置をリセットする
     */
    private void processLocomotiveDestruction(VehicleDestroyEvent event){
        UnRedstoneTeam team = UnRedstone.getInstance().data.getTeambyLocomotiveID(event.getVehicle().getUniqueId());
        if(team != null){
            event.setCancelled(true);
            event.getVehicle().teleport(team.startLocation.clone().add(0,1,0));
        }
    }
}
