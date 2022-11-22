package quarri6343.unredstone.common;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.common.logic.URLogic;
import quarri6343.unredstone.utils.UnRedstoneUtils;

/**
 * UR,MC両方のチームクラスに対する処理を行う
 */
public class GlobalTeamHandler {

    /**
     * プレイヤーをUR, MC両方のチームに入れる
     *
     * @param player プレイヤー
     * @param team   入れたいURチーム
     */
    public static void addPlayerToTeam(Player player, URTeam team) {
        team.addPlayer(player);
        MCTeams.addPlayerToMCTeam(player, team);
    }

    /**
     * プレイヤーをUR, MC両方のチームから退出させる
     */
    public static void removePlayerFromTeam(Player player) {
        URTeam team = getData().teams.getTeambyPlayer(player);
        if (team != null) {
            team.removePlayer(player);
        }
        MCTeams.removePlayerFromMCTeam(player);

        if (countAllPlayers() == 0) {
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
     * チームメンバーをチームに加入した位置にテレポートさせる
     */
    public static void teleportTeamToLobby() {
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            if (team.joinLocation1 == null || team.joinLocation2 == null)
                continue;

            Location centerLocation = UnRedstoneUtils.getCenterLocation(team.joinLocation1, team.joinLocation2);
            for (int j = 0; j < team.getPlayersSize(); j++) {
                team.getPlayer(j).teleport(centerLocation);
            }
        }
    }

    public static void resetTeams() {
        MCTeams.deleteMinecraftTeams();
        getData().teams.disbandTeams();
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
     * 参加エリアにいるプレイヤーをチームに割り当てる
     */
    public static void assignPlayersInJoinArea() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
                if (getData().teams.getTeam(i).containsPlayer(onlinePlayer)) {
                    onlinePlayer.sendMessage("既にチーム" + getData().teams.getTeam(i).name + "に加入しています！");
                    return;
                }
            }

            for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
                URTeam team = getData().teams.getTeam(i);

                if (team.joinLocation1 == null || team.joinLocation2 == null)
                    continue;

                if (UnRedstoneUtils.isPlayerInArea(onlinePlayer, team.joinLocation1, team.joinLocation2)) {
                    addPlayerToTeam(onlinePlayer, team);
                    break;
                }
            }
        }
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
