package quarri6343.unredstone.common;

import com.google.common.base.Objects;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 必要なデータを全て保存するクラス
 */
public class UnRedstoneData {

    /**
     * ゲーム管理者が現在選択しているクラス
     */
    public String adminSelectedTeam = "";

    /**
     * プレイヤーが所持できる最大のアイテム数
     */
    public int maxHoldableItems = 10;

    /**
     * 線路一本を作るのに必要な原木と丸石の数
     */
    public int craftingCost = 2;

    /**
     * ゲーム上に存在する全てのチーム
     */
    private final List<UnRedstoneTeam> teams = new ArrayList<>();

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

    /**
     * チームを登録する
     *
     * @param name  チーム名
     * @param color 　チームの色
     */
    @ParametersAreNonnullByDefault
    public void addTeam(@NotNull String name, @NotNull String color) {
        if (name.equals("")) {
            UnRedstone.getInstance().getLogger().severe("無効なチームが登録されました");
            return;
        }

        teams.add(new UnRedstoneTeam(name, color));
    }

    /**
     * チームを削除する
     *
     * @param name チーム名
     */
    @ParametersAreNonnullByDefault
    public void removeTeam(String name) {
        teams.removeIf(team -> team.name.equals(name));
    }

    /**
     * インデックスからチームを取得する
     *
     * @param index チームのインデックス
     * @return チーム
     */
    public @Nonnull
    UnRedstoneTeam getTeam(int index) {
        return teams.get(index);
    }

    /**
     * 名前からチームを取得する
     *
     * @param name チーム名
     * @return チーム
     */
    @ParametersAreNonnullByDefault
    public @Nullable UnRedstoneTeam getTeambyName(String name) {
        return teams.stream().filter(v -> v.name.equals(name)).findFirst().orElse(null);
    }

    /**
     * 色からチームを取得する
     *
     * @param color 色
     * @return チーム
     */
    public @Nullable UnRedstoneTeam getTeambyColor(NamedTextColor color) {
        return teams.stream().filter(v -> v.color.equals(NamedTextColor.NAMES.key(color))).findFirst().orElse(null);
    }

    /**
     * 色の文字列からチームを取得する
     *
     * @param color 色
     * @return チーム
     */
    @ParametersAreNonnullByDefault
    public @Nullable UnRedstoneTeam getTeambyColor(String color) {
        return teams.stream().filter(v -> v.color.equals(color)).findFirst().orElse(null);
    }

    @ParametersAreNonnullByDefault
    public @Nullable UnRedstoneTeam getTeambyLocomotiveID(UUID locomotiveID) {
        return teams.stream().filter(v -> Objects.equal(v.locomotiveID, locomotiveID)).findFirst().orElse(null);
    }

    @ParametersAreNonnullByDefault
    public @Nullable UnRedstoneTeam getTeambyPlayer(Player player) {
        return teams.stream().filter(v -> v.players.contains(player)).findFirst().orElse(null);
    }


    /**
     * チームの数を取得する
     */
    public int getTeamsLength() {
        return teams.size();
    }

    /**
     * チームを全削除する
     */
    public void clearTeam() {
        teams.clear();
    }

    /**
     * チームのプレイヤーを解散させる
     */
    public void disbandTeams() {
        for (int i = 0; i < getTeamsLength(); i++) {
            getTeam(i).players.clear();
        }
    }
}
