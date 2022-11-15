package quarri6343.unredstone.common;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.impl.ui.UIAdminMenu;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import java.util.Arrays;

import static quarri6343.unredstone.utils.UnRedstoneUtils.isInventoryTypeWhiteListed;
import static quarri6343.unredstone.utils.UnRedstoneUtils.isItemTypeBlackListed;

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
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE) {
            return;
        }

        URTeam team = getData().teams.getTeambyLocomotive(event.getVehicle());
        if (team != null) {
            event.setCancelled(true);
            event.getVehicle().teleport(team.getStartLocation().clone().add(0, 1, 0));
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GlobalTeamHandler.removePlayerFromTeam(event.getPlayer());
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
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
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

        if (itemsInInv + event.getItem().getItemStack().getAmount() <= getData().maxHoldableItems.get())
            return;

        event.setCancelled(true);
        int itemsToAdd = getData().maxHoldableItems.get() - itemsInInv;

        if (itemsToAdd > 0) {
            ((Player) event.getEntity()).getInventory().addItem(new ItemStack(material, itemsToAdd));
        }

        int itemsToDrop = event.getItem().getItemStack().getAmount() - itemsToAdd;

        if (itemsToDrop > 0) {
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(material, itemsToDrop));
        }

        event.getItem().setItemStack(new ItemStack(Material.AIR));
    }

    @org.bukkit.event.EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location respawnLocation = GlobalTeamHandler.getTeamPlayerRespawnLocation(event.getPlayer());
        if (respawnLocation != null)
            event.setRespawnLocation(respawnLocation);
    }

    @org.bukkit.event.EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        stopPortalCreate(event);
    }

    /**
     * トロッコがネザーやエンドに行くのを防ぐ
     */
    private void stopPortalCreate(PortalCreateEvent event) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        event.setCancelled(true);
    }

    @org.bukkit.event.EventHandler
    public void onCraftItem(CraftItemEvent event) {
        stopChestCrafting(event);
    }

    /**
     * バランス崩壊を防ぐためチェストをクラフトさせない
     */
    private void stopChestCrafting(CraftItemEvent event) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        Material resultMaterial = event.getRecipe().getResult().getType();
        if (resultMaterial == Material.CHEST || resultMaterial == Material.TRAPPED_CHEST) {
            event.getWhoClicked().sendMessage(Component.text("あれ？チェストってどうやって作るんだっけ？"));
            event.setCancelled(true);
        }
    }

    private URData getData() {
        return UnRedstone.getInstance().getData();
    }

    @org.bukkit.event.EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        stopRestrictedItemClick(e);
    }

    /**
     * 所持制限があるアイテムが別のインベントリでスタックされることを防ぐ
     */
    private void stopRestrictedItemClick(InventoryClickEvent e) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        if (!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null) {
            return;
        }

        if (isInventoryTypeWhiteListed(e.getView().getTopInventory().getType()))
            return;

        if (e.getClick().equals(ClickType.NUMBER_KEY)) { //handle number key click
            ItemStack slotItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
            if ((slotItem == null)) {
                return;
            }

            if (isItemTypeBlackListed(slotItem.getType()))
                e.setCancelled(true);
        } else if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)){ //handle shift key click
            if(!(e.getClickedInventory().getType() == InventoryType.PLAYER)){
                return;
            }
            
            ItemStack currentItem = e.getCurrentItem();
            if ((currentItem != null) && isItemTypeBlackListed(currentItem.getType())) {
                e.setCancelled(true);
            }
        }
        else if(!(e.getClickedInventory().getType() == InventoryType.PLAYER)){ //handle target inventory click
            ItemStack currentItem = e.getCurrentItem();
            ItemStack cursorItem = e.getCursor();
            if(cursorItem == null || !isItemTypeBlackListed(cursorItem.getType()))
                return;
            
            if(cursorItem.getType() == Material.RAIL){ //disallow put any rail into inventory
                e.setCancelled(true);
                return;
            }
            
            if (currentItem != null) {
                if(currentItem.getType() == cursorItem.getType()){
                    if(cursorItem.getAmount() + currentItem.getAmount() <= getData().maxHoldableItems.get())
                        return;
                    
                    e.setCancelled(true);
                    e.setCurrentItem(new ItemStack(currentItem.getType(), getData().maxHoldableItems.get()));
                    e.getView().getBottomInventory().addItem(new ItemStack(cursorItem.getType(),cursorItem.getAmount() + currentItem.getAmount() - getData().maxHoldableItems.get()));
                    e.setCursor(null);
                }
                else if(cursorItem.getAmount() > getData().maxHoldableItems.get())
                    e.setCancelled(true);
            }
            else if(cursorItem.getAmount() > getData().maxHoldableItems.get()){
                e.setCancelled(true);
                e.setCurrentItem(new ItemStack(cursorItem.getType(), getData().maxHoldableItems.get()));
                e.getView().getBottomInventory().addItem(new ItemStack(cursorItem.getType(),cursorItem.getAmount() - getData().maxHoldableItems.get()));
                e.setCursor(null);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        stopRestrictedItemDrag(e);
    }

    /**
     * 所持制限があるアイテムが別のインベントリでスタックされることを防ぐ
     */
    private void stopRestrictedItemDrag(InventoryDragEvent e) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        if (isInventoryTypeWhiteListed(e.getView().getTopInventory().getType())) {
            return;
        }

        ItemStack cursorItem = e.getOldCursor();
        if (isItemTypeBlackListed(cursorItem.getType())) {
            e.setCancelled(true);
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        tryExtinguishLocomotive(event);
    }

    /**
     * プレイヤーが水バケツを持ってチームのトロッコを右クリックした場合、トロッコの熱を0にする
     */
    private void tryExtinguishLocomotive(PlayerInteractEntityEvent event) {
        if ((event.getRightClicked() instanceof StorageMinecart && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WATER_BUCKET))) {
            URTeam team = getData().teams.getTeambyLocomotive(event.getRightClicked());

            if (team != null) {
                event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
                team.locomotive.extinguish();
            }
        }
    }
}
