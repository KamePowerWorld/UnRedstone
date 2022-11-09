package quarri6343.unredstone.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
import java.util.List;

/**
 * ゲームの進行を司るクラス
 */
public class UnRedstoneLogic {
    
    public GameStatus gameStatus = GameStatus.INACTIVE;
    public World gameWorld = null;
    
    private BukkitTask gameRunnable;
    private static final Material[] woods = {Material.OAK_WOOD, Material.ACACIA_WOOD, Material.BIRCH_WOOD, Material.DARK_OAK_WOOD, Material.JUNGLE_WOOD, Material.JUNGLE_WOOD};

    /**
     * ゲームを開始する
     * @param gameMaster ゲームを開始した人
     */
    public void startGame(@NotNull Player gameMaster){
        assignPlayerstoTeam();
        
        if(!canStartGame(gameMaster)){
            disbandTeams();
            return;
        }
        
        gameWorld = gameMaster.getWorld();
        gameStatus = GameStatus.ACTIVE;
        for (int i = 0; i < getData().getTeamsLength(); i++) {
            if(getData().getTeam(i).players.size() == 0)
                continue;
            
            setUpRail(getData().getTeam(i).startLocation);
            setUpRail(getData().getTeam(i).endLocation);
            Entity locomotive = gameWorld.spawnEntity(getData().getTeam(i).startLocation.clone().add(0,1,0), EntityType.MINECART_CHEST);
            locomotive.customName(Component.text("原木x2 + 丸石x2 = 線路").color(NamedTextColor.GRAY));
            getData().getTeam(i).locomotiveID = locomotive.getUniqueId();
        }
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームスタート"), Component.empty())));

        gameRunnable =  new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                count++;
                
                for (int i = 0; i < getData().getTeamsLength(); i++) {
                    if(getData().getTeam(i).locomotiveID == null)
                        continue;
                    
                    Entity locomotive = gameWorld.getEntity(getData().getTeam(i).locomotiveID);
                    if(locomotive == null)
                        continue;

                    if(locomotive.getLocation().distance(getData().getTeam(i).endLocation.clone().add(0,1,0)) < 1){
                        endGame(null, getData().getTeam(i), GameResult.SUCCESS);
                        return;
                    }

                    if(count % 40 == 0){
                        processCrafting((InventoryHolder)locomotive);
                    }
                }
            }
        }.runTaskTimer(UnRedstone.getInstance(), 0, 1);
    }
    
    private boolean canStartGame(Player gameMaster){
        if(gameStatus == GameStatus.ACTIVE){
            gameMaster.sendMessage("ゲームが進行中です！");
            return false;
        }

        if(getData().getTeamsLength() == 0){
            gameMaster.sendMessage("チームが存在しません!");
            return false;
        }
        
        int playerCount = 0;
        for (int i = 0; i < getData().getTeamsLength(); i++) {
            playerCount += getData().getTeam(i).players.size();
            
            if(getData().getTeam(i).players.size() > 0){
                if(getData().getTeam(i).startLocation == null){
                    gameMaster.sendMessage("チーム" + getData().getTeam(i).name + "の開始地点を設定してください");
                    return false;
                }
                if(getData().getTeam(i).endLocation == null){
                    gameMaster.sendMessage("チーム" + getData().getTeam(i).name + "の終了地点を設定してください");
                    return false;
                }
            }
        }
        
        if(playerCount == 0){
            gameMaster.sendMessage("誰もチームに参加していません!");
            return false;
        }
        
        return true;
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
     * インベントリの材料を消費して線路にする
     */
    private void processCrafting(InventoryHolder chest){
        Inventory chestInMinecart = chest.getInventory();
        for(Material wood : woods){
            if(chestInMinecart.containsAtLeast(new ItemStack(wood),2)
                    && chestInMinecart.containsAtLeast(new ItemStack(Material.COBBLESTONE),2)){
                chestInMinecart.removeItemAnySlot(new ItemStack(wood,2));
                chestInMinecart.removeItemAnySlot(new ItemStack(Material.COBBLESTONE,2));
                chestInMinecart.addItem(new ItemStack(Material.RAIL,1));
                break;
            }
        }
    }

    /**
     * ゲームを終了する
     * @param sender ゲームを終了した人
     * @param victoryTeam 勝ったチーム
     * @param gameResult ゲームの結果
     */
    public void endGame(@Nullable Player sender, @Nullable UnRedstoneTeam victoryTeam, GameResult gameResult){
        if(gameStatus == GameStatus.INACTIVE){
            if(sender != null)
                sender.sendMessage("ゲームが始まっていません！");
            return;
        }
        gameStatus = GameStatus.INACTIVE;
        gameRunnable.cancel();
        
        for (int i = 0; i < getData().getTeamsLength(); i++) {
            if(getData().getTeam(i).locomotiveID == null)
                continue;
            
            Entity locomotive = gameWorld.getEntity(getData().getTeam(i).locomotiveID);
            if(locomotive != null)
                locomotive.remove();
        }
        if(gameResult == GameResult.SUCCESS){
            if(victoryTeam == null){
                UnRedstone.getInstance().getLogger().severe("勝利したチームが不明です!");
                return;
            }
            
            List<TextComponent> playerList = victoryTeam.players.stream().map(player1 -> Component.text(player1.getName()).color(NamedTextColor.YELLOW)).toList();
            Component subTitle = Component.text("");
            for(int i = 0; i < playerList.size(); i++){
                if(i != 0)
                    subTitle = subTitle.append(Component.text(", ").color(NamedTextColor.YELLOW));
                subTitle = subTitle.append(playerList.get(i));
            }
            Component finalSubTitle = subTitle;
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.showTitle(Title.title(Component.text("チーム" + victoryTeam.name + "の勝利！"), finalSubTitle));
            });
        }
        else if(gameResult == GameResult.FAIL){
            Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームオーバー"), Component.empty())));
        }

        disbandTeams();
    }

    private void assignPlayerstoTeam(){
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
            for (int i = 0; i < getData().getTeamsLength(); i++) {
                if(getData().getTeam(i).players.contains(onlinePlayer)){
                    onlinePlayer.sendMessage("既にチーム" + getData().getTeam(i).name + "に加入しています！");
                    return;
                }
            }
            
            for (int i = 0; i < getData().getTeamsLength(); i++) {
                if(UnRedstoneUtils.isPlayerInArea(onlinePlayer, getData().getTeam(i).joinLocation1, getData().getTeam(i).joinLocation2)){
                    getData().getTeam(i).players.add(onlinePlayer);
                    break;
                }
            }
        }
    }
    
    private void disbandTeams(){
        for (int i = 0; i < getData().getTeamsLength(); i++) {
            getData().getTeam(i).players.clear();
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
    
    private UnRedstoneData getData(){
        return UnRedstone.getInstance().data;
    }
}
