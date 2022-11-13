package quarri6343.unredstone.common.data;

import com.google.common.base.Objects;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * チームに対する操作を行うクラス
 */
public class URTeams {

    /**
     * ゲーム上に存在する全てのチーム
     */
    private final List<URTeam> teams = new ArrayList<>();

    /**
     * チームを登録する
     *
     * @param name  チーム名
     * @param color 　チームの色
     */
    @ParametersAreNonnullByDefault
    public void addTeam(@NotNull String name, @NotNull String color) {
        if (getTeambyName(name) != null) {
            throw new IllegalArgumentException();
        }

        if (getTeambyName(color) != null) {
            throw new IllegalArgumentException();
        }

        teams.add(new URTeam(name, color));
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
    public @Nonnull URTeam getTeam(int index) {
        return teams.get(index);
    }

    /**
     * 名前からチームを取得する
     *
     * @param name チーム名
     * @return チーム
     */
    @ParametersAreNonnullByDefault
    public @Nullable URTeam getTeambyName(String name) {
        return teams.stream().filter(v -> v.name.equals(name)).findFirst().orElse(null);
    }

    /**
     * 色の文字列からチームを取得する
     *
     * @param color 色
     * @return チーム
     */
    @ParametersAreNonnullByDefault
    public @Nullable URTeam getTeambyColor(String color) {
        return teams.stream().filter(v -> v.color.equals(color)).findFirst().orElse(null);
    }

    @ParametersAreNonnullByDefault
    public @Nullable URTeam getTeambyLocomotive(Entity locomotive) {
        return teams.stream().filter(v -> v.locomotive != null && Objects.equal(v.locomotive.entity, locomotive)).findFirst().orElse(null);
    }

    @ParametersAreNonnullByDefault
    public @Nullable URTeam getTeambyPlayer(Player player) {
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
