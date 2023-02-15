package quarri6343.unredstone.common;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.impl.ui.UIAdminMenu;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import static quarri6343.unredstone.utils.UnRedstoneUtils.isInventoryTypeWhiteListed;
import static quarri6343.unredstone.utils.UnRedstoneUtils.isItemTypeBlackListed;

public class PlayerEventHandler implements Listener {

    public static final String menuItemName = "UnRedstone管理メニュー";

    public PlayerEventHandler() {
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        GlobalTeamHandler.removePlayerFromTeam(event.getPlayer(), true);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerAttemptToPickUpItem(PlayerAttemptPickupItemEvent event) {
        stopPickUpItems(event, Material.RAIL);
        for (Material wood : UnRedstoneUtils.woods) {
            stopPickUpItems(event, wood);
        }
        stopPickUpItems(event, Material.COBBLESTONE);
    }

    /**
     * プレイヤーが持てる上限を超えてアイテムを拾わないようにする
     */
    private void stopPickUpItems(PlayerAttemptPickupItemEvent event, Material material) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        if (!(event.getItem().getItemStack().getType() == material))
            return;

        int itemsInInv = 0;
        for (ItemStack itemStack : ((Player) event.getPlayer()).getInventory().all(material).values()) {
            itemsInInv += itemStack.getAmount();
        }
        ItemStack offHandItem = ((Player) event.getPlayer()).getInventory().getItemInOffHand();
        if (offHandItem.getType() == material)
            itemsInInv += offHandItem.getAmount();

        if (itemsInInv + event.getItem().getItemStack().getAmount() <= getData().maxHoldableItems.get())
            return;

        event.setCancelled(true);
        int itemsToAdd = getData().maxHoldableItems.get() - itemsInInv;

        if (itemsToAdd > 0) {
            event.getPlayer().getInventory().addItem(new ItemStack(material, itemsToAdd));
        }
        else {
            return;
        }

        int itemsToDrop = event.getItem().getItemStack().getAmount() - itemsToAdd;

        if (itemsToDrop > 0) {
            event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), new ItemStack(material, itemsToDrop));
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
    public void onCraftItem(CraftItemEvent event) {
        stopSpecialCrafting(event);
    }

    /**
     * バランス崩壊を防ぐため何種類かのアイテムをクラフトさせない
     */
    private void stopSpecialCrafting(CraftItemEvent event) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        Material resultMaterial = event.getRecipe().getResult().getType();
        if (resultMaterial == Material.CHEST || resultMaterial == Material.TRAPPED_CHEST
                || resultMaterial == Material.BARREL || resultMaterial == Material.PISTON) {
            event.getWhoClicked().sendMessage(Component.text("あれ？どうやって作るんだっけ？"));
            event.setCancelled(true);
        }
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
        } else if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) { //handle shift key click
            if (!(e.getClickedInventory().getType() == InventoryType.PLAYER)) {
                return;
            }

            ItemStack currentItem = e.getCurrentItem();
            if ((currentItem != null) && isItemTypeBlackListed(currentItem.getType())) {
                e.setCancelled(true);
            }
        } else if (!(e.getClickedInventory().getType() == InventoryType.PLAYER)) { //handle target inventory click
            ItemStack currentItem = e.getCurrentItem();
            ItemStack cursorItem = e.getCursor();
            if (cursorItem == null || !isItemTypeBlackListed(cursorItem.getType()))
                return;

            if (cursorItem.getType() == Material.RAIL) { //disallow putting any rail into inventory
                e.setCancelled(true);
                return;
            }

            if (currentItem != null) {
                if (currentItem.getType() == cursorItem.getType()) {
                    if (cursorItem.getAmount() + currentItem.getAmount() <= getData().maxHoldableItems.get())
                        return;

                    e.setCancelled(true);
                    e.setCurrentItem(new ItemStack(currentItem.getType(), getData().maxHoldableItems.get()));
                    e.getView().getBottomInventory().addItem(new ItemStack(cursorItem.getType(), cursorItem.getAmount() + currentItem.getAmount() - getData().maxHoldableItems.get()));
                    e.setCursor(null);
                } else if (cursorItem.getAmount() > getData().maxHoldableItems.get())
                    e.setCancelled(true);
            } else if (cursorItem.getAmount() > getData().maxHoldableItems.get()) {
                e.setCancelled(true);
                e.setCurrentItem(new ItemStack(cursorItem.getType(), getData().maxHoldableItems.get()));
                e.getView().getBottomInventory().addItem(new ItemStack(cursorItem.getType(), cursorItem.getAmount() - getData().maxHoldableItems.get()));
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
                event.getPlayer().playSound(Sound.sound(Key.key("entity.player.swim"), Sound.Source.BLOCK, 1f, 1f));
                event.setCancelled(true);
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if(stopInteractBlockNearLocation(event))
            return;
        
        stopInteractPassedRails(event);
    }

    /**
     * ゲームのスタート・ゴール地点周辺で壊されてはいけないブロックが壊されることを阻止する
     * @return 阻止されたかどうか
     */
    private boolean stopInteractBlockNearLocation(BlockBreakEvent event) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return false;

        if (event.getPlayer().isOp())
            return false;

        Block block = event.getBlock();
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            if (getData().teams.getTeam(i).getStartLocation().distance(block.getLocation()) > URData.respawnProtectionRange
                    && getData().teams.getTeam(i).getEndLocation().distance(block.getLocation()) > URData.respawnProtectionRange)
                continue;

            for (Material material : UnRedstoneUtils.blocksToProtectNearStartLocation) {
                if (block.getType() == material) {
                    event.getPlayer().sendMessage(Component.text("スタート/ゴール地点の保護対象ブロックです"));
                    event.setCancelled(true);
                    return true;
                }
            }

            if (getData().teams.getTeam(i).getStartLocation().getBlock().equals(block)
                    || getData().teams.getTeam(i).getStartLocation().clone().subtract(0, 1, 0).getBlock().equals(event.getBlock())) {
                event.getPlayer().sendMessage(Component.text("スタート地点は壊せません"));
                event.setCancelled(true);
                return true;
            }
            if (getData().teams.getTeam(i).getEndLocation().getBlock().equals(block)
                    || getData().teams.getTeam(i).getEndLocation().clone().subtract(0, 1, 0).getBlock().equals(event.getBlock())) {
                event.getPlayer().sendMessage(Component.text("ゴール地点は壊せません"));
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }

    /**
     * トロッコが既に通過した場所のレールが壊されることを防ぐ
     * @param event
     */
    private void stopInteractPassedRails(BlockBreakEvent event){
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        if (event.getPlayer().isOp())
            return;
        
        if(event.getBlock().getType() != Material.RAIL)
            return;

        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            if(getData().teams.getTeam(i).locomotive == null)
                continue;
            
            if(getData().teams.getTeam(i).locomotive.isLocationPassed(event.getBlock().getLocation())){
                event.getPlayer().sendMessage(Component.text("通過済みの線路は壊せません"));
                event.setCancelled(true);
                return;
            }
        }
    }

    private URData getData() {
        return UnRedstone.getInstance().getData();
    }
}
