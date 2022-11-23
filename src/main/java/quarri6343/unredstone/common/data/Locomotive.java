package quarri6343.unredstone.common.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static quarri6343.unredstone.utils.UnRedstoneUtils.woods;

/**
 * 線路の上を走る目標オブジェクトのクラス
 */
public class Locomotive {

    public final Entity entity;
    public final Heat heat = new Heat();
    private final List<Location> passedLocations = new ArrayList<>();

    private static final int burnRadius = 4;
    private static final int burnChance = 5;

    public Locomotive(@Nonnull Entity entity) {
        if (!(entity instanceof StorageMinecart)) {
            throw new IllegalArgumentException();
        }

        entity.setCustomNameVisible(true);
        ((InventoryHolder) entity).getInventory().addItem(new ItemStack(Material.BUCKET));
        this.entity = entity;
    }

    /**
     * インベントリの材料を消費して線路にする
     */
    public void processCrafting(URData data) {
        Inventory inventory = ((InventoryHolder) entity).getInventory();

        if (inventory.containsAtLeast(new ItemStack(Material.RAIL), data.maxHoldableItems.get()))
            return;

        for (Material wood : woods) {
            if (inventory.containsAtLeast(new ItemStack(wood), data.craftingCost.get())
                    && inventory.containsAtLeast(new ItemStack(Material.COBBLESTONE), data.craftingCost.get())) {
                inventory.removeItemAnySlot(new ItemStack(wood, data.craftingCost.get()));
                inventory.removeItemAnySlot(new ItemStack(Material.COBBLESTONE, data.craftingCost.get()));
                inventory.addItem(new ItemStack(Material.RAIL, 1));
                break;
            }
        }
    }

    /**
     * 特定のアイテムを持ちすぎていた場合、ドロップさせる
     */
    public void dropExcessiveItems(Material material, int maxHoldableItems) {
        Inventory inventory = ((InventoryHolder) entity).getInventory();

        int itemsInInv = 0;
        for (ItemStack itemStack : inventory.all(material).values()) {
            itemsInInv += itemStack.getAmount();
        }

        if (itemsInInv <= maxHoldableItems) {
            return;
        }

        inventory.removeItemAnySlot(new ItemStack(material, itemsInInv - maxHoldableItems));
        if (inventory.getLocation() != null) {
            entity.getWorld().dropItemNaturally(inventory.getLocation(), new ItemStack(material, itemsInInv - maxHoldableItems));
        }
    }

    /**
     * トロッコに1ポイント加熱する処理を行わせる
     */
    public void addHeat() {
        if (!heat.add()) {
            burn();
            return;
        }

        updateHeatDisplay();
    }

    /**
     * トロッコの熱を0に戻す
     */
    public void extinguish() {
        heat.reset();

        updateHeatDisplay();
    }

    /**
     * トロッコに溜まった熱の表示を更新する
     */
    private void updateHeatDisplay() {
        entity.customName(Component.text("熱:").color(NamedTextColor.WHITE).append(heat.getHeatAsString()));
    }

    /**
     * トロッコが熱を貯めきれなくなった時の燃える処理
     */
    private void burn() {
        int x = entity.getLocation().getBlockX();
        int y = entity.getLocation().getBlockY();
        int z = entity.getLocation().getBlockZ();

        for (int i = x - burnRadius; i <= x + burnRadius; i++) {
            for (int j = y - burnRadius; j <= y + burnRadius; j++) {
                for (int k = z - burnRadius; k <= z + burnRadius; k++) {
                    Block block = entity.getWorld().getBlockAt(i, j, k);

                    if (block.getType() != Material.AIR && block.getRelative(BlockFace.UP).getType() == Material.AIR
                            && block.getRelative(BlockFace.UP).getLocation().getNearbyEntities(1, 1, 1).size() == 0) {
                        if (new Random().nextInt(100) <= burnChance) {
                            entity.getWorld().setType(i, j + 1, k, Material.FIRE);
                        }
                    }
                }
            }
        }
    }

    public void removeEntitySafely() {
        ((InventoryHolder) entity).getInventory().clear();
        entity.remove();
    }

    /**
     * トロッコが通過した座標を記録する
     *
     * @param location 通過した座標
     */
    public void addPassedLocation(Location location) {
        Location blockPos = location.toBlockLocation();
        for (Location passedLocation : passedLocations) {
            if (passedLocation.getX() == blockPos.getX()
                    && passedLocation.getY() == blockPos.getY()
                    && passedLocation.getZ() == blockPos.getZ()) {
                return;
            }
        }
        passedLocations.add(blockPos);
    }

    /**
     * トロッコが座標を通過したか判定する
     * @param location 判定したい座標
     * @return 通過したかどうか
     */
    public boolean isLocationPassed(Location location){
        Location blockPos = location.toBlockLocation();
        for (Location passedLocation : passedLocations) {
            if (passedLocation.getX() == blockPos.getX()
                    && passedLocation.getY() == blockPos.getY()
                    && passedLocation.getZ() == blockPos.getZ()) {
                return true;
            }
        }
        return false;
    }

    /**
     * トロッコが通過した座標を全て消去する
     */
    public void clearPassedLocation(){
        passedLocations.clear();
    }
}
