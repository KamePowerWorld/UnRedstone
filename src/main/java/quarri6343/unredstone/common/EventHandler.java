package quarri6343.unredstone.common;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.impl.UIMenu;

public class EventHandler implements Listener {
    
    public static final String menuItemName = "UnRedstone管理メニュー";
    
    public EventHandler(){
        UnRedstone.getInstance().getServer().getPluginManager().registerEvents(this, UnRedstone.getInstance());
    }
    
    @org.bukkit.event.EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().getType().equals(Material.STICK) && event.getItem().getItemMeta().getDisplayName().equals(menuItemName)){
            UIMenu.openUI(event.getPlayer());
            event.setCancelled(true);
        }
    }
    
    @org.bukkit.event.EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event){
        if(event.getVehicle().getUniqueId().equals(UnRedstone.getInstance().logic.locomotiveID)){
            event.setCancelled(true);
            event.getVehicle().teleport(UnRedstone.getInstance().data.getTeam(0).startLocation.clone().add(0,1,0));
        }
    }
}
