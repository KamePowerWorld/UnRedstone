package quarri6343.unredstone.common.data;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

import static quarri6343.unredstone.utils.UnRedstoneUtils.woods;

/**
 * 線路の上を走る目標オブジェクトのクラス
 */
public class Locomotive {
    public final Entity entity;
    
    public Locomotive(@Nonnull Entity entity){
        if(!(entity instanceof StorageMinecart)){
            throw new IllegalArgumentException();
        }

        this.entity = entity;
    }

    /**
     * インベントリの材料を消費して線路にする
     */
    public void processCrafting(URData data) {
        Inventory inventory = ((InventoryHolder)entity).getInventory();

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
     * アイテムを持ちすぎていた場合、ドロップさせる
     */
    public void dropExcessiveItems(Material material, int maxHoldableItems) {
        Inventory inventory = ((InventoryHolder)entity).getInventory();
        
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
}
