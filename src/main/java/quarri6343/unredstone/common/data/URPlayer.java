package quarri6343.unredstone.common.data;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * チームに所属するプレイヤーのデータクラス
 */
public class URPlayer {

    public final Player entity;

    /**
     * プレイヤーがチームに入る前のゲームモード
     */
    private final GameMode lastGameMode;

    public URPlayer(@Nonnull Player entity){
        this.entity = entity;
        this.lastGameMode = entity.getGameMode();
    }

    /**
     * プレイヤーのゲームモードをチーム参加前に戻す
     */
    public void restoreGameMode(){
        entity.setGameMode(lastGameMode);
    }
}
