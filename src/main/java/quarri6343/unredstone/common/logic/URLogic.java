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
import quarri6343.unredstone.common.GlobalTeamHandler;
import quarri6343.unredstone.common.data.Locomotive;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static quarri6343.unredstone.common.data.URData.gameResultSceneLength;

/**
 * ゲームの進行を司るクラス
 */
public class URLogic {

    public GameStatus gameStatus = GameStatus.INACTIVE;
    public World gameWorld = null;
    private BukkitTask gameBeginRunnable;
    private BukkitTask gameRunnable;
    private BukkitTask gameEndRunnable;

    /**
     * ゲームを開始する
     *
     * @param gameMaster ゲームを開始した人
     */
    public void startGame(@NotNull Player gameMaster) {
        if (gameStatus != GameStatus.INACTIVE) {
            gameMaster.sendMessage("ゲームが進行中です！");
            return;
        }

        GlobalTeamHandler.assignPlayersInJoinArea();

        if (!GlobalTeamHandler.areTeamsValid(gameMaster)) {
            GlobalTeamHandler.resetTeams();
            return;
        }

        gameWorld = gameMaster.getWorld();
        gameStatus = GameStatus.BEGINNING;
        gameBeginRunnable = new GameBeginRunnable(this::onGameBegin).runTaskTimer(UnRedstone.getInstance(), 0, 1);
    }

    /**
     * ゲームが実際に始まった時に行う処理
     */
    private void onGameBegin() {
        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            if (team.getPlayersSize() == 0)
                continue;

            setUpRail(team.getStartLocation());
            setUpRail(team.getEndLocation());
            Entity locomotive = gameWorld.spawnEntity(team.getStartLocation().clone().add(0, 1, 0), EntityType.MINECART_CHEST);
            team.locomotive = new Locomotive(locomotive);

            for (int j = 0; j < team.getPlayersSize(); j++) {
                team.setUpGameEnvforPlayer(team.getPlayer(j));
            }
        }
        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(Title.title(Component.text("ゲームスタート"), Component.empty())));

        gameStatus = GameStatus.ACTIVE;
        gameRunnable = new GameRunnable(urTeam -> endGame(null, urTeam, URLogic.GameResult.SUCCESS, true)).runTaskTimer(UnRedstone.getInstance(), 0, 1);
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
    public void endGame(@Nullable Player sender, @Nullable URTeam victoryTeam, GameResult gameResult, boolean hasResultScene) {
        if (gameStatus == GameStatus.INACTIVE) {
            if (sender != null)
                sender.sendMessage("ゲームが始まっていません！");
            return;
        }

        if (gameBeginRunnable != null)
            gameBeginRunnable.cancel();
        if (gameRunnable != null)
            gameRunnable.cancel();
        if (gameEndRunnable != null)
            gameEndRunnable.cancel();

        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            if (getData().teams.getTeam(i).locomotive == null)
                continue;

            getData().teams.getTeam(i).locomotive.removeEntitySafely();
            getData().teams.getTeam(i).locomotive = null;
        }
        if (gameResult == GameResult.SUCCESS) {
            displayGameSuccessTitle(victoryTeam);
        } else if (gameResult == GameResult.FAIL) {
            displayGameFailureTitle();
        }

        gameStatus = GameStatus.ENDING;
        if (hasResultScene)
            gameEndRunnable = new GameEndRunnable(() -> gameStatus = URLogic.GameStatus.INACTIVE, true).runTaskTimer(UnRedstone.getInstance(), gameResultSceneLength, 1);
        else
            new GameEndRunnable(() -> gameStatus = URLogic.GameStatus.INACTIVE, false).run();
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

        List<TextComponent> playerList = victoryTeam.playerNamesToText();
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
        BEGINNING,
        ACTIVE,
        ENDING,
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
        return UnRedstone.getInstance().getData();
    }
}
