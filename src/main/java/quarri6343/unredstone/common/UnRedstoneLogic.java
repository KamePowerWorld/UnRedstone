package quarri6343.unredstone.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * ゲームの進行を司るクラス
 */
public class UnRedstoneLogic {
    
    public GameStatus gameStatus = GameStatus.INACTIVE;
    public World gameWorld = null;
    public UUID locomotiveID;

    private BukkitTask gameRunnable;

    /**
     * ゲームを開始する
     * @param gameMaster ゲームを開始した人
     */
    public void startGame(@NotNull Player gameMaster){
        if(gameStatus == GameStatus.ACTIVE){
            gameMaster.sendMessage("ゲームが進行中です！");
            return;
        }
        
        UnRedstoneData data = UnRedstone.getInstance().data;
        if(data.getTeamsLength() == 0){
            gameMaster.sendMessage("チームが存在しません!");
            return;
        }
        if(data.getTeam(0).startLocation == null){
            gameMaster.sendMessage("開始地点を設定してください");
            return;
        }
        if(data.getTeam(0) == null){
            gameMaster.sendMessage("終了地点を設定してください");
            return;
        }
        
        gameWorld = gameMaster.getWorld();
        gameStatus = GameStatus.ACTIVE;
        setUpRail(data.getTeam(0).startLocation);
        setUpRail(data.getTeam(0).endLocation);
        Entity locomotive = gameWorld.spawnEntity(data.getTeam(0).startLocation.clone().add(0,1,0), EntityType.MINECART_CHEST);
        locomotive.customName(Component.text("原木x2 + 丸石x2 = 線路").color(NamedTextColor.GRAY));
        locomotiveID = locomotive.getUniqueId();
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームスタート"), Component.empty())));

        gameRunnable =  new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                Entity locomotive = gameWorld.getEntity(locomotiveID);
                if(locomotive != null){
                    if(locomotive.getLocation().distance(data.getTeam(0).endLocation.clone().add(0,1,0)) < 1){
                        endGame(null, GameResult.SUCCESS);
                        return;
                    }
                }
                else{
                    endGame(null, GameResult.FAIL);
                    return;
                }
                
                count++;
                if(count % 40 == 0){
                    Inventory chestInMinecart = ((InventoryHolder)locomotive).getInventory();
                    if(chestInMinecart.containsAtLeast(new ItemStack(Material.OAK_WOOD),2) 
                            && chestInMinecart.containsAtLeast(new ItemStack(Material.COBBLESTONE),2)){
                        chestInMinecart.removeItemAnySlot(new ItemStack(Material.OAK_WOOD,2));
                        chestInMinecart.removeItemAnySlot(new ItemStack(Material.COBBLESTONE,2));
                        chestInMinecart.addItem(new ItemStack(Material.RAIL,1));
                    }
                }
            }
        }.runTaskTimer(UnRedstone.getInstance(), 0, 1);
    }

    /**
     * 指定した場所と向きにレールを設置する
     * @param location レールを置きたい場所と向き
     */
    @ParametersAreNonnullByDefault
    private void setUpRail(Location location){
        gameWorld.setType(location, Material.RAIL);
        Rail rail = (Rail) (gameWorld.getBlockAt(location).getBlockData());
        rail.setShape(UnRedstoneUtils.yawToRailShape(location.getYaw()));
        gameWorld.setBlockData(location, rail);
        gameWorld.setType(location.clone().subtract(0,1,0),Material.DIRT);
    }

    /**
     * ゲームを終了する
     * @param sender ゲームを終了した人
     * @param gameResult ゲームの結果
     */
    public void endGame(@Nullable Player sender, GameResult gameResult){
        if(gameStatus == GameStatus.INACTIVE){
            if(sender != null)
                sender.sendMessage("ゲームが始まっていません！");
            return;
        }

        gameStatus = GameStatus.INACTIVE;
        gameRunnable.cancel();
        
        if(gameWorld.getEntity(locomotiveID) != null)
            gameWorld.getEntity(locomotiveID).remove();
        if(gameResult == GameResult.SUCCESS){
            Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームクリア"), Component.empty())));
        }
        else if(gameResult == GameResult.FAIL){
            Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームオーバー"), Component.empty())));
        }
    }

    /**
     * ゲームの状態(進行中/始まっていない)
     */
    public enum GameStatus{
        ACTIVE,
        INACTIVE
    }

    /**
     * ゲームの結果(成功/失敗)
     */
    public enum GameResult
    {
        SUCCESS,
        FAIL;
    }
}
