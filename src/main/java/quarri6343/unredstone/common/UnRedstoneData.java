package quarri6343.unredstone.common;

import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quarri6343.unredstone.UnRedstone;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * ゲームが進行中でない時も必要なデータを全て保存するクラス
 */
public class UnRedstoneData {
    public String adminSelectedTeam = "";
    private List<UnRedStoneTeam> teams = new ArrayList<>();

    /**
     * チームを登録する
     * @param name チーム名
     * @param color　チームの色
     */
    @ParametersAreNonnullByDefault
    public void addTeam(@NotNull String name, @NotNull String color){
        if(name.equals("")){
            UnRedstone.getInstance().getLogger().severe("無効なチームが登録されました");
            return;
        }

        teams.add(new UnRedStoneTeam(name, color));
    }

    /**
     * チームを削除する
     * @param name チーム名
     */
    @ParametersAreNonnullByDefault
    public void removeTeam(String name){
        teams.removeIf(team -> team.name.equals(name));
    }

    /**
     * インデックスからチームを取得する
     * @param index チームのインデックス
     * @return チーム
     */
    public @Nonnull UnRedStoneTeam getTeam(int index){
        return teams.get(index);
    }

    /**
     * 名前からチームを取得する
     * @param name チーム名
     * @return チーム
     */
    @ParametersAreNonnullByDefault
    public @Nullable UnRedStoneTeam getTeambyName(String name){
        return teams.stream().filter(v -> v.name.equals(name)).findFirst().orElse(null);
    }

    /**
     * 色からチームを取得する
     * @param color 色
     * @return チーム
     */
    public @Nullable UnRedStoneTeam getTeambyColor(NamedTextColor color){
        return teams.stream().filter(v -> v.color.equals(NamedTextColor.NAMES.key(color))).findFirst().orElse(null);
    }

    /**
     * 色の文字列からチームを取得する
     * @param color 色
     * @return チーム
     */
    @ParametersAreNonnullByDefault
    public @Nullable UnRedStoneTeam getTeambyColor(String color){
        return teams.stream().filter(v -> v.color.equals(color)).findFirst().orElse(null);
    }

    /**
     * チームの数を取得する
     * @return
     */
    public int getTeamsLength(){
        return teams.size();
    }

    /**
     * チームを全削除する
     */
    public void clearTeam(){
        teams.clear();
    }
}
