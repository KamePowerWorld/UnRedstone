package quarri6343.unredstone.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;

public class UnRedstoneUtils {

    public static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static final Material[] woods = {Material.OAK_WOOD, Material.ACACIA_WOOD, Material.BIRCH_WOOD, Material.DARK_OAK_WOOD, Material.JUNGLE_WOOD, Material.JUNGLE_WOOD};

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
     * Locationのブロック座標を文字列に変換する
     */
    @ParametersAreNonnullByDefault
    public static String locationBlockPostoString(Location location) {
        return "x=" + location.getBlockX() + ",y=" + location.getBlockY() + ",z=" + location.getBlockZ();
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

    public static boolean isPlayerInArea(Player player, Location location1, Location location2) {
        double playerX = player.getLocation().getX();
        double playerZ = player.getLocation().getZ();

        boolean isXInRange = Math.min(location1.getX(), location2.getX()) <= playerX
                && Math.max(location1.getX(), location2.getX()) >= playerX;
        boolean isZInRange = Math.min(location1.getZ(), location2.getZ()) <= playerZ
                && Math.max(location1.getZ(), location2.getZ()) >= playerZ;

        if (isXInRange && isZInRange)
            return true;
        else
            return false;
    }
}
