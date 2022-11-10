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

import static quarri6343.unredstone.common.UnRedstoneData.*;
import static quarri6343.unredstone.utils.UnRedstoneUtils.randomizeLocation;
import static quarri6343.unredstone.utils.UnRedstoneUtils.woods;

/**
 * ゲームの進行を司るクラス
 */
public class UnRedstoneLogic {

    public GameStatus gameStatus = GameStatus.INACTIVE;
    public World gameWorld = null;
    private BukkitTask gameRunnable;

    /**
     * ゲームを開始する
     *
     * @param gameMaster ゲームを開始した人
     */
    public void startGame(@NotNull Player gameMaster) {
        assignPlayerstoTeam();

        if (!canStartGame(gameMaster)) {
            getData().disbandTeams();
            return;
        }

        gameWorld = gameMaster.getWorld();
        gameStatus = GameStatus.ACTIVE;
        for (int i = 0; i < getData().getTeamsLength(); i++) {
            if (getData().getTeam(i).players.size() == 0)
                continue;

            setUpRail(getData().getTeam(i).startLocation);
            setUpRail(getData().getTeam(i).endLocation);
            Entity locomotive = gameWorld.spawnEntity(getData().getTeam(i).startLocation.clone().add(0, 1, 0), EntityType.MINECART_CHEST);
            locomotive.customName(Component.text("原木x" + craftingCost + " + 丸石x" + craftingCost + " = 線路").color(NamedTextColor.GRAY));
            getData().getTeam(i).locomotiveID = locomotive.getUniqueId();

            for (Player player : getData().getTeam(i).players) {
                player.teleport(randomizeLocation(getData().getTeam(i).startLocation));
            }
        }
        UnRedstone.getInstance().scoreBoardManager.createTeamFromData();
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームスタート"), Component.empty())));

        gameRunnable = new GameRunnable().runTaskTimer(UnRedstone.getInstance(), 0, 1);
    }

    /**
     * ゲームを開始できるか判定する
     *
     * @param gameMaster ゲーム開始者
     * @return ゲームを開始できるか
     */
    private boolean canStartGame(Player gameMaster) {
        if (gameStatus == GameStatus.ACTIVE) {
            gameMaster.sendMessage("ゲームが進行中です！");
            return false;
        }

        if (getData().getTeamsLength() == 0) {
            gameMaster.sendMessage("チームが存在しません!");
            return false;
        }

        int playerCount = 0;
        for (int i = 0; i < getData().getTeamsLength(); i++) {
            playerCount += getData().getTeam(i).players.size();

            if (getData().getTeam(i).players.size() > 0) {
                if (getData().getTeam(i).startLocation == null) {
                    gameMaster.sendMessage("チーム" + getData().getTeam(i).name + "の開始地点を設定してください");
                    return false;
                }
                if (getData().getTeam(i).endLocation == null) {
                    gameMaster.sendMessage("チーム" + getData().getTeam(i).name + "の終了地点を設定してください");
                    return false;
                }
            }
        }

        if (playerCount == 0) {
            gameMaster.sendMessage("誰もチームに参加していません!");
            return false;
        }

        return true;
    }

    /**
     * 指定した場所と向きにレールを設置する
     *
     * @param location レールを置きたい場所と向き
     */
    @ParametersAreNonnullByDefault
    private void setUpRail(Location location) {
        gameWorld.setType(location, Material.RAIL);
        Rail rail = (Rail) (gameWorld.getBlockAt(location).getBlockData());
        rail.setShape(UnRedstoneUtils.yawToRailShape(location.getYaw()));
        gameWorld.setBlockData(location, rail);
        gameWorld.setType(location.clone().subtract(0, 1, 0), Material.DIRT);
    }

    /**
     * ゲームを終了する
     *
     * @param sender      ゲームを終了した人
     * @param victoryTeam 勝ったチーム
     * @param gameResult  ゲームの結果
     */
    public void endGame(@Nullable Player sender, @Nullable UnRedstoneTeam victoryTeam, GameResult gameResult) {
        if (gameStatus == GameStatus.INACTIVE) {
            if (sender != null)
                sender.sendMessage("ゲームが始まっていません！");
            return;
        }
        gameRunnable.cancel();

        for (int i = 0; i < getData().getTeamsLength(); i++) {
            if (getData().getTeam(i).locomotiveID == null)
                continue;

            Entity locomotive = gameWorld.getEntity(getData().getTeam(i).locomotiveID);
            if (locomotive != null)
                locomotive.remove();
        }
        if (gameResult == GameResult.SUCCESS) {
            displayGameSuccessTitle(victoryTeam);
        } else if (gameResult == GameResult.FAIL) {
            displayGameFailureTitle();
        }

        new GameEndRunnable().runTaskTimer(UnRedstone.getInstance(), gameResultSceneLength, 1);
    }

    /**
     * 参加エリアにいるプレイヤーをチームに割り当てる
     */
    private void assignPlayerstoTeam() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < getData().getTeamsLength(); i++) {
                if (getData().getTeam(i).players.contains(onlinePlayer)) {
                    onlinePlayer.sendMessage("既にチーム" + getData().getTeam(i).name + "に加入しています！");
                    return;
                }
            }

            for (int i = 0; i < getData().getTeamsLength(); i++) {
                if (getData().getTeam(i).joinLocation1 == null || getData().getTeam(i).joinLocation2 == null)
                    continue;

                if (UnRedstoneUtils.isPlayerInArea(onlinePlayer, getData().getTeam(i).joinLocation1, getData().getTeam(i).joinLocation2)) {
                    getData().getTeam(i).players.add(onlinePlayer);
                    break;
                }
            }
        }
    }

    /**
     * ゲームが成功したというタイトルを表示する
     *
     * @param victoryTeam 勝利したチーム
     */
    private void displayGameSuccessTitle(UnRedstoneTeam victoryTeam) {
        if (victoryTeam == null) {
            UnRedstone.getInstance().getLogger().severe("勝利したチームが不明です!");
            return;
        }

        List<TextComponent> playerList = victoryTeam.players.stream().map(player1 -> Component.text(player1.getName()).color(NamedTextColor.YELLOW)).toList();
        Component subTitle = Component.text("");
        for (int i = 0; i < playerList.size(); i++) {
            if (i != 0)
                subTitle = subTitle.append(Component.text(", ").color(NamedTextColor.YELLOW));
            subTitle = subTitle.append(playerList.get(i));
        }
        Component finalSubTitle = subTitle;
        Bukkit.getOnlinePlayers().forEach(player ->
                player.showTitle(Title.title(Component.text("チーム" + victoryTeam.name + "の勝利！"), finalSubTitle)));
    }

    /**
     * ゲームが失敗したというタイトルを表示する
     */
    private void displayGameFailureTitle() {
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームオーバー"), Component.empty())));
    }

    /**
     * ゲームの状態(進行中/始まっていない)
     */
    public enum GameStatus {
        ACTIVE,
        INACTIVE
    }

    /**
     * ゲームの結果(成功/失敗)
     */
    public enum GameResult {
        SUCCESS,
        FAIL
    }

    private UnRedstoneData getData() {
        return UnRedstone.getInstance().data;
    }

    /**
     * 定期的に起動してゲームの状態を監視するrunnable
     */
    private class GameRunnable extends BukkitRunnable {
        int count = 0;

        @Override
        public void run() {
            count++;

            for (int i = 0; i < getData().getTeamsLength(); i++) {
                if (getData().getTeam(i).locomotiveID == null)
                    continue;

                Entity locomotive = gameWorld.getEntity(getData().getTeam(i).locomotiveID);
                if (locomotive == null)
                    continue;

                if (locomotive.getLocation().distance(getData().getTeam(i).endLocation.clone().add(0, 1, 0)) < 1) {
                    endGame(null, getData().getTeam(i), UnRedstoneLogic.GameResult.SUCCESS);
                    return;
                }

                if (count % checkInventoryInterval == 0) {
                    dropItems((InventoryHolder) locomotive, Material.RAIL);
                    for (Material wood : UnRedstoneUtils.woods) {
                        dropItems((InventoryHolder) locomotive, wood);
                    }
                    dropItems((InventoryHolder) locomotive, Material.COBBLESTONE);
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

            if (chestInMinecart.containsAtLeast(new ItemStack(Material.RAIL), maxHoldableItems))
                return;

            for (Material wood : woods) {
                if (chestInMinecart.containsAtLeast(new ItemStack(wood), craftingCost)
                        && chestInMinecart.containsAtLeast(new ItemStack(Material.COBBLESTONE), craftingCost)) {
                    chestInMinecart.removeItemAnySlot(new ItemStack(wood, craftingCost));
                    chestInMinecart.removeItemAnySlot(new ItemStack(Material.COBBLESTONE, craftingCost));
                    chestInMinecart.addItem(new ItemStack(Material.RAIL, 1));
                    break;
                }
            }
        }

        /**
         * プレイヤーやトロッコがアイテムを持ちすぎていた場合、ドロップさせる
         */
        private void dropItems(InventoryHolder chest, Material material) {
            for (int i = 0; i < getData().getTeamsLength(); i++) {
                for (int j = 0; j < getData().getTeam(i).players.size(); j++) {
                    Player player = getData().getTeam(i).players.get(i);

                    int itemsInInv = 0;
                    for (ItemStack itemStack : getData().getTeam(i).players.get(i).getInventory().all(material).values()) {
                        itemsInInv += itemStack.getAmount();
                    }

                    ItemStack offHandItem = getData().getTeam(i).players.get(i).getInventory().getItemInOffHand();
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

                if (itemsInInv <= maxHoldableItems) {
                    continue;
                }

                chest.getInventory().removeItemAnySlot(new ItemStack(material, itemsInInv - maxHoldableItems));
                if (chest.getInventory().getLocation() != null) {
                    gameWorld.dropItemNaturally(chest.getInventory().getLocation(), new ItemStack(material, itemsInInv - maxHoldableItems));
                }
            }
        }
    }

    /**
     * ゲーム終了後時間を空けて行いたい処理
     */
    private class GameEndRunnable extends BukkitRunnable {

        @Override
        public void run() {
            teleportTeamToLobby();
            UnRedstone.getInstance().scoreBoardManager.deleteTeam();
            getData().disbandTeams();
            gameStatus = GameStatus.INACTIVE;
            cancel();
        }

        /**
         * チームメンバーをチームに加入した位置にテレポートさせる
         */
        private void teleportTeamToLobby() {
            for (int i = 0; i < getData().getTeamsLength(); i++) {
                if (getData().getTeam(i).joinLocation1 == null || getData().getTeam(i).joinLocation2 == null)
                    continue;

                Location centerLocation = UnRedstoneUtils.getCenterLocation(getData().getTeam(i).joinLocation1, getData().getTeam(i).joinLocation2);
                for (Player player : getData().getTeam(i).players) {
                    player.teleport(centerLocation);
                }
            }
        }
    }
}
