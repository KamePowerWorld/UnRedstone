package quarri6343.unredstone.common.logic;

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
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.data.Locomotive;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static quarri6343.unredstone.common.data.URData.*;
import static quarri6343.unredstone.utils.UnRedstoneUtils.randomizeLocation;

/**
 * ゲームの進行を司るクラス
 */
public class URLogic {

    public GameStatus gameStatus = GameStatus.INACTIVE;
    public World gameWorld = null;
    private BukkitTask gameRunnable;

    /**
     * ゲームを開始する
     *
     * @param gameMaster ゲームを開始した人
     */
    public void startGame(@NotNull Player gameMaster) {
        if (gameStatus == GameStatus.ACTIVE) {
            gameMaster.sendMessage("ゲームが進行中です！");
            return;
        }
        
        UnRedstone.getInstance().globalTeamHandler.assignPlayersInJoinArea();

        if (!UnRedstone.getInstance().globalTeamHandler.areTeamsValid(gameMaster)) {
            UnRedstone.getInstance().globalTeamHandler.resetTeams();
            return;
        }

        gameWorld = gameMaster.getWorld();
        gameStatus = GameStatus.ACTIVE;
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            if (team.players.size() == 0)
                continue;

            setUpRail(team.getStartLocation());
            setUpRail(team.getEndLocation());
            Entity locomotive = gameWorld.spawnEntity(team.getStartLocation().clone().add(0, 1, 0), EntityType.MINECART_CHEST);
            locomotive.customName(Component.text("原木x" + getData().craftingCost.get() + " + 丸石x" + getData().craftingCost.get() + " = 線路").color(NamedTextColor.GRAY));
            team.locomotive = new Locomotive(locomotive);

            for (Player player : team.players) {
                player.teleport(randomizeLocation(team.getStartLocation()));
            }
        }
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームスタート"), Component.empty())));

        gameRunnable = new GameRunnable(urTeam -> endGame(null, urTeam, URLogic.GameResult.SUCCESS)).runTaskTimer(UnRedstone.getInstance(), 0, 1);
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
    public void endGame(@Nullable Player sender, @Nullable URTeam victoryTeam, GameResult gameResult) {
        if (gameStatus == GameStatus.INACTIVE) {
            if (sender != null)
                sender.sendMessage("ゲームが始まっていません！");
            return;
        }
        
        if(gameRunnable != null)
            gameRunnable.cancel();

        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            if (getData().teams.getTeam(i).locomotive == null)
                continue;

            getData().teams.getTeam(i).locomotive.entity.remove();
            getData().teams.getTeam(i).locomotive = null;
        }
        if (gameResult == GameResult.SUCCESS) {
            displayGameSuccessTitle(victoryTeam);
        } else if (gameResult == GameResult.FAIL) {
            displayGameFailureTitle();
        }

        new GameEndRunnable(() -> gameStatus = URLogic.GameStatus.INACTIVE).runTaskTimer(UnRedstone.getInstance(), gameResultSceneLength, 1);
    }

    /**
     * ゲームが成功したというタイトルを表示する
     *
     * @param victoryTeam 勝利したチーム
     */
    private void displayGameSuccessTitle(URTeam victoryTeam) {
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

    private URData getData() {
        return UnRedstone.getInstance().data;
    }
}
