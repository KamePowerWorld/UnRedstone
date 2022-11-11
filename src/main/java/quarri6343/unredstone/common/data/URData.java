package quarri6343.unredstone.common.data;

import quarri6343.unredstone.api.stackSizeInt;

/**
 * 必要なデータを全て保存するクラス
 */
public class URData {

    /**
     * ゲーム管理者が現在選択しているクラスの名前
     */
    public String adminSelectedTeam = "";

    /**
     * プレイヤーが所持できる最大のアイテム数
     */
    public stackSizeInt maxHoldableItems = new stackSizeInt(10);

    /**
     * 線路一本を作るのに必要な原木と丸石の数
     */
    public stackSizeInt craftingCost = new stackSizeInt(2);

    public final URTeams teams = new URTeams();

    /**
     * ゲームのリザルトシーンの長さ
     */
    public static final int gameResultSceneLength = 100;

    /**
     * ゲームがプレイヤーのインベントリを確認する周期
     */
    public static final int checkInventoryInterval = 20;

    /**
     * トロッコがレールをクラフトする周期
     */
    public static final int craftRailInterval = 40;

    /**
     * プレイヤーがある場所にスポーンするときスポーン地点をどれだけランダム化するか
     */
    public static final int randomSpawnMagnitude = 5;
}
