package quarri6343.unredstone.common;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * UR,MC両方のチームクラスに対する処理を行う
 */
public class GlobalTeamHandler {
    private final Map<UUID, Team> playerTeams = new HashMap<>();

    public GlobalTeamHandler() {
        // 毎ティックプレイヤーのチームの変更を監視
        Bukkit.getScheduler().runTaskTimer(UnRedstone.getInstance(), this::updatePlayerTeams, 0, 1);
    }

    /**
     * プレイヤーのチームを更新する
     */
    public void updatePlayerTeams() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            Team newTeam = player.getScoreboard().getPlayerTeam(player);
            Team oldTeam = playerTeams.get(player.getUniqueId());
            // チームが変わった場合、スコアボードのチームに合わせる (nullの場合は削除)
            // その際、URTeamにも追加する
            if (newTeam != oldTeam) {
                // まず元いたチームから抜ける
                if (oldTeam != null) {
                    URData data = UnRedstone.getInstance().getData();
                    URTeam urTeam = data.teams.getTeambyName(oldTeam.getName());
                    if (urTeam != null) {
                        urTeam.removePlayer(player, newTeam != null);
                    }
                }

                // 新しいチームに入る
                if (newTeam != null) {
                    URData data = UnRedstone.getInstance().getData();
                    URTeam urTeam = data.teams.getTeambyName(newTeam.getName());
                    if (urTeam == null) {
                        urTeam.addPlayer(player);
                    }
                }

                // プレイヤーのチームを更新
                playerTeams.put(player.getUniqueId(), newTeam);
            }
        }
    }

    /**
     * プレイヤーをUR, MC両方のチームから退出させる
     */
    public static void removePlayerFromTeam(Player player, boolean restoreStats) {
        URTeam team = getData().teams.getTeambyPlayer(player);
        if (team != null) {
            team.removePlayer(player, restoreStats);
        }

        if (getLogic().gameStatus == URLogic.GameStatus.ACTIVE && countAllPlayers() == 0) {
            getLogic().endGame(null, null, URLogic.GameResult.FAIL, true);
        }
    }

    /**
     * 死んだプレイヤーのチームのトロッコの近くのリスポーン位置を取得
     */
    public static Location getTeamPlayerRespawnLocation(Player player) {
        if (UnRedstone.getInstance().getLogic().gameStatus == URLogic.GameStatus.INACTIVE)
            return null;

        URTeam team = getData().teams.getTeambyPlayer(player);
        if (team == null || team.locomotive == null)
            return null;

        Entity locomotive = team.locomotive.entity;

        return UnRedstoneUtils.randomizeLocation(locomotive.getLocation());
    }

    /**
     * チームの状態がゲームを開始できる状態にあるか判定する
     *
     * @param gameMaster ゲーム開始者
     * @return ゲームを開始できるか
     */
    public static boolean areTeamsValid(Player gameMaster) {
        if (getData().teams.getTeamsLength() == 0) {
            gameMaster.sendMessage("チームが存在しません!");
            return false;
        }

        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);

            if (team.getPlayersSize() > 0) {
                if (team.getStartLocation() == null) {
                    gameMaster.sendMessage("チーム" + team.name + "の開始地点を設定してください");
                    return false;
                }
                if (team.getEndLocation() == null) {
                    gameMaster.sendMessage("チーム" + team.name + "の終了地点を設定してください");
                    return false;
                }
            }
        }

        if (countAllPlayers() == 0) {
            gameMaster.sendMessage("誰もチームに参加していません!");
            return false;
        }

        return true;
    }

    /**
     * チームに参加している全てのプレイヤーをカウントする
     *
     * @return 全てのチームのプレイヤー数の合計
     */
    public static int countAllPlayers() {
        int playerCount = 0;
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            playerCount += getData().teams.getTeam(i).getPlayersSize();
        }

        return playerCount;
    }

    /**
     * チーム中のプレイヤーが指定アイテムを持ちすぎていた場合、ドロップさせる
     */
    public static void dropExcessiveItems(Material material, int maxHoldableItems) {
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            for (int j = 0; j < team.getPlayersSize(); j++) {
                Player player = team.getPlayer(j);

                int itemsInInv = 0;
                for (ItemStack itemStack : player.getInventory().all(material).values()) {
                    itemsInInv += itemStack.getAmount();
                }

                ItemStack offHandItem = player.getInventory().getItemInOffHand();
                if (offHandItem.getType() == material)
                    itemsInInv += offHandItem.getAmount();

                if (itemsInInv > maxHoldableItems) {
                    player.getInventory().removeItemAnySlot(new ItemStack(material, itemsInInv - maxHoldableItems));
                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(material, itemsInInv - maxHoldableItems));
                }
            }
        }
    }

    /**
     * 全てのチームの全てのプレイヤーにポーション効果を配る
     *
     * @param type
     * @param duration
     * @param level
     */
    public static void giveEffectToPlayers(PotionEffectType type, int duration, int level) {
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            for (int j = 0; j < team.getPlayersSize(); j++) {
                team.getPlayer(j).addPotionEffect(new PotionEffect(type, duration, level - 1, false, false));
            }
        }
    }

    /**
     * チームの開始/終了地点直下にビーコンを置く
     *
     * @param team
     */
    public static void placeBeaconBelowTeamLocations(URTeam team) {
        Location startLocation = team.getStartLocation();
        Location endLocation = team.getEndLocation();
        if (startLocation == null || endLocation == null)
            throw new IllegalArgumentException();

        startLocation.getWorld().setType(startLocation.clone().add(0, -1, 0), UnRedstoneUtils.namedTextColorToGlassType(team.color));
        startLocation.getWorld().setType(startLocation.clone().add(0, -2, 0), Material.BEACON);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                startLocation.getWorld().setType(startLocation.clone().add(i, -3, j), Material.IRON_BLOCK);
            }
        }

        endLocation.getWorld().setType(endLocation.clone().add(0, -1, 0), UnRedstoneUtils.namedTextColorToGlassType(team.color));
        endLocation.getWorld().setType(endLocation.clone().add(0, -2, 0), Material.BEACON);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                endLocation.getWorld().setType(endLocation.clone().add(i, -3, j), Material.IRON_BLOCK);
            }
        }
    }

    /**
     * プレイヤーにヘッドライトを付与する。LightAPIの1.19.2への対応待ち
     *
     * @param length
     */
    public static void giveHeadLight(int length) {
//        List<Location> locationList = new ArrayList<>();
//        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
//            URTeam team = getData().teams.getTeam(i);
//            for (int j = 0; j < team.getPlayersSize(); j++) {
//                Location location = team.getPlayer(j).getLocation();
//                LightAPI.get().setLightLevel(team.getPlayer(j).getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), 15);
//                locationList.add(location);
//            }
//        }
//        
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                locationList.forEach(location -> LightAPI.get().setLightLevel(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0));
//                cancel();
//            }
//        }.runTaskTimer(UnRedstone.getInstance(), length, 1);
    }

    private static URData getData() {
        return UnRedstone.getInstance().getData();
    }

    private static URLogic getLogic() {
        return UnRedstone.getInstance().getLogic();
    }
}
