package quarri6343.unredstone;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import java.util.UUID;

public class UnRedstoneLogic {
    
    public static Location startLocation = null;
    public static Location relayLocation1 = null;
    public static Location relayLocation2 = null;
    public static Location endLocation = null;
    
    public static GameStatus gameStatus = GameStatus.INACTIVE;
    public static World gameWorld = null;
    public static UUID locomotiveID;

    private static BukkitTask gameRunnable;
    
    public static void startGame(@NotNull Player gameMaster){
        if(gameStatus == GameStatus.ACTIVE){
            gameMaster.sendMessage("ゲームが進行中です！");
            return;
        }
        if(startLocation == null){
            gameMaster.sendMessage("開始地点を設定してください");
            return;
        }
        if(endLocation == null){
            gameMaster.sendMessage("終了地点を設定してください");
            return;
        }
        
        gameWorld = gameMaster.getWorld();
        gameStatus = GameStatus.ACTIVE;
        setUpRail(startLocation);
        setUpRail(endLocation);
        locomotiveID = gameWorld.spawnEntity(startLocation.clone().add(0,1,0), EntityType.MINECART_CHEST).getUniqueId();
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームスタート"), Component.empty())));

        gameRunnable =  new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                Entity locomotive = gameWorld.getEntity(locomotiveID);
                if(locomotive != null){
                    if(locomotive.getLocation().distance(endLocation.clone().add(0,1,0)) < 1){
                        endGame(null, GameResult.SUCCESS);
                        return;
                    }
                }
                else{
                    endGame(null, GameResult.FAIL);
                    return;
                }
                
                count++;
                if(count % 4 == 0){
                    Inventory chestInMinecart = ((InventoryHolder)locomotive).getInventory();
                    if(chestInMinecart.containsAtLeast(new ItemStack(Material.LEGACY_LOG),2) 
                            && chestInMinecart.containsAtLeast(new ItemStack(Material.COBBLESTONE),2)){
                        chestInMinecart.removeItemAnySlot(new ItemStack(Material.LEGACY_LOG,2));
                        chestInMinecart.removeItemAnySlot(new ItemStack(Material.COBBLESTONE,2));
                    }
                }
            }
        }.runTaskTimer(UnRedstone.getInstance(), 0, 10);
    }
    
    private static void setUpRail(Location location){
        gameWorld.setType(location, Material.RAIL);
        Rail rail = (Rail) (gameWorld.getBlockAt(location).getBlockData());
        rail.setShape(UnRedstoneUtils.yawToRailShape(location.getYaw()));
        gameWorld.setBlockData(location, rail);
        gameWorld.setType(location.clone().subtract(0,1,0),Material.DIRT);
    }
    
    public static void endGame(@Nullable Player sender, GameResult gameResult){
        if(gameStatus == GameStatus.INACTIVE){
            if(sender != null)
                sender.sendMessage("ゲームが始まっていません！");
            return;
        }

        gameStatus = GameStatus.INACTIVE;
        gameRunnable.cancel();
        
        if(gameResult == GameResult.SUCCESS){
            Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームクリア"), Component.empty())));
        }
        else if(gameResult == GameResult.FAIL){
            Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームオーバー"), Component.empty())));
        }
    }

    public enum GameStatus{
        ACTIVE,
        INACTIVE
    }

    public enum GameResult
    {
        SUCCESS,
        FAIL;
    }
}
