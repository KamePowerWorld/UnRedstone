package quarri6343.unredstone.common.data;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UnRedstoneを共に攻略するチームのデータクラス
 */
public class URTeam {

    public final String name;
    public final String color;
    
    private Location startLocation;
    private Location endLocation;
    
    public Location joinLocation1;
    public Location joinLocation2;
    public UUID locomotiveID;

    public List<Player> players = new ArrayList<>();

    public URTeam(String name, String color) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        if(NamedTextColor.NAMES.value(color) == null){
            throw new IllegalArgumentException();
        }
        
        this.name = name;
        this.color = color;
    }
    
    public Location getStartLocation(){
        return startLocation;
    }
    
    public Location getEndLocation(){
        return endLocation;
    }

    /**
     * 開始位置が終了位置と近すぎないか判定する
     * @param location 開始位置
     * @return 開始位置が大丈夫かどうか
     */
    public boolean isStartLocationValid(@Nullable Location location){
        if(location == null || endLocation == null)
            return true;

        return location.distance(endLocation) >= 1;
    }

    /**
     * 終了位置が開始位置と近すぎないか判定する
     * @param location 終了位置
     * @return 終了位置が大丈夫かどうか
     */
    public boolean isEndLocationValid(@Nullable Location location){
        if(location == null || startLocation == null)
            return true;

        return location.distance(startLocation) >= 1;
    }
    
    public void setStartLocation(Location location){
        if(!isStartLocationValid(location))
            throw new IllegalArgumentException();
        
        startLocation = location;
    }

    public void setEndLocation(Location location){
        if(!isEndLocationValid(location))
            throw new IllegalArgumentException();

        endLocation = location;
    }
}
