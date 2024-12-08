package quarri6343.unredstone.common;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import quarri6343.unredstone.common.logic.URLogic;

public class MCScores {
    private static Objective objective;

    /**
     * スコアボードを初期化
     */
    public static void setupObjective() {
        Scoreboard board = getBoard();
        objective = board.getObjective("unredstone");
        if (objective == null) {
            objective = board.registerNewObjective("unredstone", "dummy", "UnRedstone");
        }
    }

    /**
     * ゲームのステータスを設定
     *
     * @param status ゲームのステータス
     */
    public static void setGameStatus(URLogic.GameStatus status) {
        objective.getScore("status").setScore(status.ordinal());
    }

    /**
     * ゲームのステータスを取得
     *
     * @return ゲームのステータス
     */
    public static URLogic.GameStatus getGameStatus() {
        URLogic.GameStatus[] statuses = URLogic.GameStatus.values();
        var statusIndex = objective.getScore("status").getScore();
        if (statusIndex < 0 || statusIndex >= statuses.length) {
            return URLogic.GameStatus.INACTIVE;
        }
        return statuses[statusIndex];
    }

    private static Scoreboard getBoard() {
        return Bukkit.getScoreboardManager().getMainScoreboard();
    }
}
