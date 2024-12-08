package quarri6343.unredstone.common.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static quarri6343.unredstone.utils.UnRedstoneUtils.randomizeLocation;

/**
 * UnRedstoneを共に攻略するチームのデータクラス
 */
public class URTeam {

    public final String name;
    public final String color;

    private Location startLocation;
    private Location endLocation;

    public Locomotive locomotive;

    private final List<URPlayer> players = new ArrayList<>();

    public URTeam(String name, String color) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (NamedTextColor.NAMES.value(color) == null) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.color = color;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    /**
     * 開始位置が終了位置と近すぎないか判定する
     *
     * @param location 開始位置
     * @return 開始位置が大丈夫かどうか
     */
    public boolean isStartLocationValid(@Nullable Location location) {
        if (location == null || endLocation == null)
            return true;

        return location.distance(endLocation) >= 1;
    }

    /**
     * 終了位置が開始位置と近すぎないか判定する
     *
     * @param location 終了位置
     * @return 終了位置が大丈夫かどうか
     */
    public boolean isEndLocationValid(@Nullable Location location) {
        if (location == null || startLocation == null)
            return true;

        return location.distance(startLocation) >= 1;
    }

    /**
     * チームに所属しているプレイヤーの環境をゲーム開始に適した状態に変更する
     *
     * @param player チームに所属しているプレイヤー
     */
    public void setUpGameEnvforPlayer(Player player) {
        if (!containsPlayer(player))
            return;

        player.teleport(randomizeLocation(locomotive.entity.getLocation()));
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setContents(new ItemStack[]{});
        player.setSaturation(5f);
        player.setFoodLevel(20);
    }

    public void setStartLocation(Location location) {
        if (!isStartLocationValid(location))
            throw new IllegalArgumentException();

        startLocation = location;
    }

    public void setEndLocation(Location location) {
        if (!isEndLocationValid(location))
            throw new IllegalArgumentException();

        endLocation = location;
    }

    public void addPlayer(Player player) {
        if(!containsPlayer(player))
            players.add(new URPlayer(player));
    }

    public Player getPlayer(int index) {
        return players.get(index).entity;
    }

    public void removePlayer(Player player, boolean restoreStats) {
        URPlayer playerToRemove = players.stream().filter(urPlayer -> urPlayer.entity.equals(player)).findFirst().orElse(null);
        if (playerToRemove == null) {
            return;
        }

        if (restoreStats)
            playerToRemove.restoreStats();
        players.remove(playerToRemove);
    }

    public void removeAllPlayer(boolean restoreStats) {
        if (restoreStats)
            for (URPlayer player : players) {
                player.restoreStats();
            }
        players.clear();
    }

    public int getPlayersSize() {
        return players.size();
    }

    public boolean containsPlayer(Player player) {
        return players.stream().filter(urPlayer -> urPlayer.entity.equals(player)).findFirst().orElse(null) != null;
    }

    public List<TextComponent> playerNamesToText() {
        return players.stream().map(player1 -> Component.text(player1.entity.getName()).color(NamedTextColor.YELLOW)).toList();
    }
}
