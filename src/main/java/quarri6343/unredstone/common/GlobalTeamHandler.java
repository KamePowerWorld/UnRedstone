package quarri6343.unredstone.common;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URPlayer;
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
    public void addPlayerToTeam(Player player, URTeam team) {
        team.addPlayer(player);
        UnRedstone.getInstance().mcTeamHandler.addPlayerToMCTeam(player, team);
    }

    /**
     * プレイヤーをUR, MC両方のチームから退出させる
     */
    public void removePlayerFromTeam(Player player) {
        URTeam team = getData().teams.getTeambyPlayer(player);
        if (team != null) {
            team.removePlayer(player);
        }
        UnRedstone.getInstance().mcTeamHandler.removePlayerFromMCTeam(player);
    }

    /**
     * 死んだプレイヤーをチームのトロッコの近くにスポーンさせる
     */
    public void respawnPlayerNearLocomotive(PlayerRespawnEvent event) {
        if (UnRedstone.getInstance().logic.gameStatus == URLogic.GameStatus.INACTIVE)
            return;

        URTeam team = getData().teams.getTeambyPlayer(event.getPlayer());
        if (team == null || team.locomotive == null)
            return;

        Entity locomotive = team.locomotive.entity;

        event.setRespawnLocation(UnRedstoneUtils.randomizeLocation(locomotive.getLocation()));
    }

    /**
     * チームメンバーをチームに加入した位置にテレポートさせる
     */
    public void teleportTeamToLobby() {
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

    public void resetTeams() {
        UnRedstone.getInstance().mcTeamHandler.deleteMinecraftTeams();
        getData().teams.disbandTeams();
    }

    /**
     * チームの状態がゲームを開始できる状態にあるか判定する
     *
     * @param gameMaster ゲーム開始者
     * @return ゲームを開始できるか
     */
    public boolean areTeamsValid(Player gameMaster) {
        if (getData().teams.getTeamsLength() == 0) {
            gameMaster.sendMessage("チームが存在しません!");
            return false;
        }

        int playerCount = 0;
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            playerCount += team.getPlayersSize();

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

        if (playerCount == 0) {
            gameMaster.sendMessage("誰もチームに参加していません!");
            return false;
        }

        return true;
    }

    /**
     * 参加エリアにいるプレイヤーをチームに割り当てる
     */
    public void assignPlayersInJoinArea() {
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
    public void dropExcessiveItems(URTeam team, Material material, int maxHoldableItems) {
        for (int i = 0; i < team.getPlayersSize(); i++) {
            Player player = team.getPlayer(i);

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

    private URData getData() {
        return UnRedstone.getInstance().data;
    }
}
