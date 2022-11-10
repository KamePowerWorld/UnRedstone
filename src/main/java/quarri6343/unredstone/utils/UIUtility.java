package quarri6343.unredstone.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;

public class UIUtility {
    
    public static final TextComponent gameRunningText = Component.text("ゲームが進行中です！").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
    public static final TextComponent teamNotSelectedText = Component.text("チームが選択されていません")
            .color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);

    /**
     * @param location 文章にしたいLocation
     * @return 渡されたLocationの情報を表す文
     */
    public static TextComponent getLocDesc(Location location) {
        return Component.text(location != null ? "現在：" + UnRedstoneUtils.locationBlockPostoString(location) : "未設定です")
                .color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
    }
}
