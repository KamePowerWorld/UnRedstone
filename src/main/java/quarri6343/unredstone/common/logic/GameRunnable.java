package quarri6343.unredstone.common.logic;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import java.util.function.Consumer;

import static quarri6343.unredstone.common.data.URData.checkInventoryInterval;
import static quarri6343.unredstone.common.data.URData.craftRailInterval;
import static quarri6343.unredstone.utils.UnRedstoneUtils.woods;

/**
 * 定期的に起動してゲームの状態を監視するrunnable
 */
public class GameRunnable extends BukkitRunnable {
    
    private int count = 0;
    private final World gameWorld;
    private final Consumer<URTeam> onGameSuccess;

    public GameRunnable(World gameWorld, Consumer<URTeam> onGameSuccess){
        this.gameWorld = gameWorld;
        this.onGameSuccess = onGameSuccess;
    }
    
    @Override
    public void run() {
        count++;

        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            if (team.locomotiveID == null)
                continue;

            Entity locomotive = gameWorld.getEntity(team.locomotiveID);
            if (locomotive == null)
                continue;

            if (locomotive.getLocation().distance(team.getEndLocation().clone().add(0, 1, 0)) < 1) {
                onGameSuccess.accept(team);
                return;
            }

            if (count % checkInventoryInterval == 0) {
                dropExcessiveItems((InventoryHolder) locomotive, Material.RAIL);
                for (Material wood : UnRedstoneUtils.woods) {
                    dropExcessiveItems((InventoryHolder) locomotive, wood);
                }
                dropExcessiveItems((InventoryHolder) locomotive, Material.COBBLESTONE);
            }
            if (count % craftRailInterval == 0) {
                processCrafting((InventoryHolder) locomotive);
            }
        }
    }

    /**
     * インベントリの材料を消費して線路にする
     */
    private void processCrafting(InventoryHolder chest) {
        Inventory chestInMinecart = chest.getInventory();

        if (chestInMinecart.containsAtLeast(new ItemStack(Material.RAIL), getData().maxHoldableItems.get()))
            return;

        for (Material wood : woods) {
            if (chestInMinecart.containsAtLeast(new ItemStack(wood), getData().craftingCost.get())
                    && chestInMinecart.containsAtLeast(new ItemStack(Material.COBBLESTONE), getData().craftingCost.get())) {
                chestInMinecart.removeItemAnySlot(new ItemStack(wood, getData().craftingCost.get()));
                chestInMinecart.removeItemAnySlot(new ItemStack(Material.COBBLESTONE, getData().craftingCost.get()));
                chestInMinecart.addItem(new ItemStack(Material.RAIL, 1));
                break;
            }
        }
    }

    /**
     * プレイヤーやトロッコがアイテムを持ちすぎていた場合、ドロップさせる
     */
    private void dropExcessiveItems(InventoryHolder chest, Material material) {
        int maxHoldableItems = getData().maxHoldableItems.get();
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);

            for (int j = 0; j < team.players.size(); j++) {
                Player player = team.players.get(j);

                int itemsInInv = 0;
                for (ItemStack itemStack : team.players.get(j).getInventory().all(material).values()) {
                    itemsInInv += itemStack.getAmount();
                }

                ItemStack offHandItem = team.players.get(j).getInventory().getItemInOffHand();
                if (offHandItem.getType() == material)
                    itemsInInv += offHandItem.getAmount();

                if (itemsInInv > maxHoldableItems) {
                    player.getInventory().removeItemAnySlot(new ItemStack(material, itemsInInv - maxHoldableItems));
                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(material, itemsInInv - maxHoldableItems));
                }
            }

            int itemsInInv = 0;
            for (ItemStack itemStack : chest.getInventory().all(material).values()) {
                itemsInInv += itemStack.getAmount();
            }

            if (itemsInInv <= getData().maxHoldableItems.get()) {
                continue;
            }

            chest.getInventory().removeItemAnySlot(new ItemStack(material, itemsInInv - maxHoldableItems));
            if (chest.getInventory().getLocation() != null) {
                gameWorld.dropItemNaturally(chest.getInventory().getLocation(), new ItemStack(material, itemsInInv - maxHoldableItems));
            }
        }
    }
    
    private URData getData() {
        return UnRedstone.getInstance().data;
    }
}
