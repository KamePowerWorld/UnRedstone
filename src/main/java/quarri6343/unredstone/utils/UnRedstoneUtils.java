package quarri6343.unredstone.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.SimplePluginManager;
import quarri6343.unredstone.common.data.URData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;

public class UnRedstoneUtils {

    public static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static final Material[] woods = {Material.OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.JUNGLE_LOG, Material.SPRUCE_LOG};
    public static final InventoryType[] whiteListedInventories = {InventoryType.PLAYER, InventoryType.CHEST, InventoryType.CRAFTING, InventoryType.WORKBENCH};

    /**
     * スタート、終了地点付近でこれらのブロックが壊されるのを禁止する
     */
    public static final Material[] blocksToProtectNearStartLocation = {Material.BEACON, Material.IRON_BLOCK};

    /**
     * プラグインマネージャからコマンドマップを取得する
     *
     * @return 取得したコマンドマップ
     */
    @Nullable
    public static CommandMap getCommandMap() {
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);

                return (CommandMap) field.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * プレイヤーの向きをレールの向きに変換する
     */
    public static Rail.Shape yawToRailShape(float yaw) {
        BlockFace face = axis[Math.round(yaw / 90f) & 0x3];
        if (face == BlockFace.NORTH || face == BlockFace.SOUTH)
            return Rail.Shape.NORTH_SOUTH;
        else
            return Rail.Shape.EAST_WEST;
    }

    /**
     * ある範囲のx,z空間内の中心点を見つける
     *
     * @param location1 範囲の始点
     * @param location2 範囲の終点
     * @return 範囲の中心点
     */
    @ParametersAreNonnullByDefault
    public static @Nonnull
    Location getCenterLocation(Location location1, Location location2) {
        return new Location(location1.getWorld(),
                (location1.getX() + location2.getX()) / 2,
                (location1.getY() + location2.getY()) / 2,
                (location1.getZ() + location2.getZ()) / 2);
    }

    /**
     * ある場所のx,z座標をランダム化する
     *
     * @param location ランダム化したい場所
     * @return ランダム化された場所
     */
    @ParametersAreNonnullByDefault
    public static Location randomizeLocation(Location location) {
        int magnitude = URData.randomSpawnMagnitude;
        int x = new Random().nextInt(magnitude * 2 + 1) - magnitude;
        int y = 0;
        int z = new Random().nextInt(magnitude * 2 + 1) - magnitude;
        return location.clone().add(x, y, z);
    }

    /**
     * インベントリが所持制限を適用されないかどうか
     *
     * @param type インベントリの種類
     */
    public static boolean isInventoryTypeWhiteListed(InventoryType type) {
        return Arrays.stream(whiteListedInventories).filter(inventoryType -> type == inventoryType).findFirst().orElse(null) != null;
    }

    /**
     * 所持制限があるアイテムの種類であるかどうか
     *
     * @param type アイテムの種類
     */
    public static boolean isItemTypeBlackListed(Material type) {
        return Arrays.stream(woods).filter(material -> type == material).findFirst().orElse(null) != null
                || type == Material.COBBLESTONE || type == Material.RAIL;
    }

    /**
     * namedTextColorをマイクラのガラスの種類に変換する
     *
     * @param color NamedTextColorのString版
     * @return ガラスの種類
     */
    public static Material namedTextColorToGlassType(String color) {
        return switch (color) {
            case "yellow", "gold" -> Material.YELLOW_STAINED_GLASS;
            case "aqua", "dark_aqua" -> Material.CYAN_STAINED_GLASS;
            case "black" -> Material.BLACK_STAINED_GLASS;
            case "blue", "dark_blue" -> Material.BLUE_STAINED_GLASS;
            case "gray", "dark_gray" -> Material.GRAY_STAINED_GLASS;
            case "dark_green", "green" -> Material.GREEN_STAINED_GLASS;
            case "light_purple", "dark_purple" -> Material.PURPLE_STAINED_GLASS;
            case "dark_red", "red" -> Material.RED_STAINED_GLASS;
            case "white" -> Material.WHITE_STAINED_GLASS;
            default -> null;
        };
    }
}
