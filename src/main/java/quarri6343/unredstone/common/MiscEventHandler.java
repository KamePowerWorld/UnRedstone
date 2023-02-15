package quarri6343.unredstone.common;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.PortalCreateEvent;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.common.logic.URLogic;

public class MiscEventHandler implements Listener {

    public MiscEventHandler() {
        UnRedstone.getInstance().getServer().getPluginManager().registerEvents(this, UnRedstone.getInstance());
    }


    @org.bukkit.event.EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        processLocomotiveDestruction(event);
    }

    /**
     * トロッコが破壊された場合、チームの初期地点に位置をリセットする
     */
    private void processLocomotiveDestruction(VehicleDestroyEvent event) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE) {
            return;
        }

        URTeam team = getData().teams.getTeambyLocomotive(event.getVehicle());
        if (team != null) {
            event.setCancelled(true);
            event.getVehicle().teleport(team.getStartLocation().clone().add(0, 1, 0));
            team.locomotive.clearPassedLocation();
        }
    }

    @org.bukkit.event.EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        stopRailDrop(event);
    }

    /**
     * バランス崩壊防止のためにレールを壊したときドロップさせないようにする
     */
    private void stopRailDrop(BlockDropItemEvent event) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE) {
            return;
        }

        if (event.getItems().stream().filter
                (item -> item.getItemStack().getType() == Material.RAIL).findAny().orElse(null) != null) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        stopPortalCreation(event);
    }

    /**
     * トロッコがネザーやエンドに行くのを防ぐ
     */
    private void stopPortalCreation(PortalCreateEvent event) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        processLocomotiveMovement(event);
    }

    /**
     * トロッコが移動時の処理を行う
     */
    private void processLocomotiveMovement(VehicleMoveEvent event) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        URTeam team = getData().teams.getTeambyLocomotive(event.getVehicle());
        if (team == null)
            return;

        Material toType = event.getTo().getBlock().getType();
        Material upType = event.getTo().getBlock().getRelative(BlockFace.UP).getType();
        Material downType = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
        if (toType == Material.RAIL
                || toType == Material.POWERED_RAIL
                || toType == Material.DETECTOR_RAIL
                || upType == Material.RAIL
                || upType == Material.POWERED_RAIL
                || upType == Material.DETECTOR_RAIL
                || downType == Material.RAIL
                || downType == Material.POWERED_RAIL
                || downType == Material.DETECTOR_RAIL) {
            team.locomotive.addPassedLocation(event.getFrom());
        } else{
            team.locomotive.entity.teleport(event.getFrom());
        }
    }
    
    @EventHandler
    private void onEntityExplode(EntityExplodeEvent event){
        stopRailBreakFromExplosion(event);
    }

    /**
     * レールが爆発で破壊されることを阻止する
     */
    private void stopRailBreakFromExplosion(EntityExplodeEvent event){
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        event.blockList().stream().filter(block -> block.getType() == Material.RAIL
        || block.getType() == Material.POWERED_RAIL || block.getType() == Material.DETECTOR_RAIL).findAny().ifPresent(rail -> event.setCancelled(true));
    }

    private URData getData() {
        return UnRedstone.getInstance().getData();
    }
}
