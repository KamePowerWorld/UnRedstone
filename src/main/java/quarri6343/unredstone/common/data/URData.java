package quarri6343.unredstone.common.data;

import quarri6343.unredstone.api.RangedInt;

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
    public final RangedInt maxHoldableItems = new RangedInt(10, 1, 64);

    /**
     * 線路一本を作るのに必要な原木と丸石の数
     */
    public final RangedInt craftingCost = new RangedInt(2, 1, 64);

    /**
     * 試合中人数補正のためプレイヤーに任意で付与する補正バフの強度
     */
    public final RangedInt buffStrength = new RangedInt(0, 0, 5);

    public final URTeams teams = new URTeams();

    /**
     * ゲームが始まるまでのカウントダウンの長さ
     */
    public static final int gameBeginCountdownLength = 100;

    /**
     * ゲームのリザルトシーンの長さ
     */
    public static final int gameResultSceneLength = 100;

    /**
     * ゲームがプレイヤーのインベントリを確認する周期
     */
    public static final int checkInventoryInterval = 20;

    /**
     * ゲームがプレイヤーにバフを付与する周期
     */
    public static final int giveBuffInterval = 20;

    /**
     * トロッコがレールをクラフトする周期
     */
    public static final int craftRailInterval = 40;

    /**
     * プレイヤーがある場所にスポーンするときスポーン地点をどれだけランダム化するか
     */
    public static final int randomSpawnMagnitude = 3;

    /**
     * トロッコを1ポイント加熱する周期
     */
    public static final int heatLocomotiveInterval = 40;

    /**
     * プレイヤーにヘッドライトを配る周期
     */
    public static final int headLightInterval = 5;
}
