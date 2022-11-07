package quarri6343.unredstone.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class UnRedstoneUtils {

    public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    /**
     * プラグインマネージャからコマンドマップを取得する
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
    
    public static String locationBlockPostoString(Location location){
        return "x=" + location.getBlockX() + ",y=" + location.getBlockY() + ",z=" + location.getBlockZ();
    }
    
    public static Rail.Shape yawToRailShape(float yaw) {
        BlockFace face = axis[Math.round(yaw / 90f) & 0x3];
        if(face == BlockFace.NORTH || face == BlockFace.SOUTH)
            return Rail.Shape.NORTH_SOUTH;
        else
            return Rail.Shape.EAST_WEST;
    }
}
