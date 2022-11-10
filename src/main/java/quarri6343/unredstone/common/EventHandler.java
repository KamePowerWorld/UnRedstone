package quarri6343.unredstone.common;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.impl.ui.UIAdminMenu;
import quarri6343.unredstone.utils.UnRedstoneUtils;

public class EventHandler implements Listener {

    public static final String menuItemName = "UnRedstone管理メニュー";

    public EventHandler() {
        UnRedstone.getInstance().getServer().getPluginManager().registerEvents(this, UnRedstone.getInstance());
    }

    @org.bukkit.event.EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        processHandheldItem(event);
    }

    /**
     * 手持ちのアイテムを識別してそれに対応したguiを開く
     *
     * @param event
     */
    private void processHandheldItem(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().getType().equals(Material.STICK)
                    && event.getItem().getItemMeta().getDisplayName().equals(menuItemName) && event.getPlayer().isOp()) {
                UIAdminMenu.openUI(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        processLocomotiveDestruction(event);
    }

    /**
     * トロッコが破壊された場合、チームの初期地点に位置をリセットする
     */
    private void processLocomotiveDestruction(VehicleDestroyEvent event) {
        if (UnRedstone.getInstance().logic.gameStatus == UnRedstoneLogic.GameStatus.INACTIVE) {
            return;
        }

        UnRedstoneTeam team = getData().getTeambyLocomotiveID(event.getVehicle().getUniqueId());
        if (team != null) {
            event.setCancelled(true);
            event.getVehicle().teleport(team.startLocation.clone().add(0, 1, 0));
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        kickPlayerfromTeam(event.getPlayer());
    }

    /**
     * プレイヤーがゲームを退出した時、バグ防止のためチームから退出させる
     *
     * @param player
     */
    private void kickPlayerfromTeam(Player player) {
        UnRedstoneTeam team = getData().getTeambyPlayer(player);
        if (team != null) {
            team.players.remove(player);
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
        if (UnRedstone.getInstance().logic.gameStatus == UnRedstoneLogic.GameStatus.INACTIVE) {
            return;
        }

        if (event.getItems().stream().filter
                (item -> item.getItemStack().getType() == Material.RAIL).findAny().orElse(null) != null) {
            event.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        stopPickUpItems(event, Material.RAIL);
        for (Material wood : UnRedstoneUtils.woods) {
            stopPickUpItems(event, wood);
        }
        stopPickUpItems(event, Material.COBBLESTONE);
    }

    /**
     * プレイヤーが持てる上限を超えてアイテムを拾わないようにする
     */
    private void stopPickUpItems(EntityPickupItemEvent event, Material material) {
        if (UnRedstone.getInstance().logic.gameStatus == UnRedstoneLogic.GameStatus.INACTIVE)
            return;

        if (!(event.getItem().getItemStack().getType() == material))
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        int itemsInInv = 0;
        for (ItemStack itemStack : ((Player) event.getEntity()).getInventory().all(material).values()) {
            itemsInInv += itemStack.getAmount();
        }
        ItemStack offHandItem = ((Player) event.getEntity()).getInventory().getItemInOffHand();
        if (offHandItem.getType() == material)
            itemsInInv += offHandItem.getAmount();

        if (itemsInInv + event.getItem().getItemStack().getAmount() <= getData().maxHoldableItems)
            return;

        event.setCancelled(true);
        int itemsToAdd = getData().maxHoldableItems - itemsInInv;

        if (itemsToAdd > 0) {
            ((Player) event.getEntity()).getInventory().addItem(new ItemStack(material, itemsToAdd));
        }

        int itemsToDrop = event.getItem().getItemStack().getAmount() - itemsToAdd;

        if (itemsToDrop > 0) {
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(material, itemsToDrop));
        }

        event.getItem().setItemStack(new ItemStack(Material.AIR));
    }

    private UnRedstoneData getData() {
        return UnRedstone.getInstance().data;
    }
}
