package quarri6343.unredstone.common.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * トロッコの熱を司るクラス
 */
public class Heat {

    private int value = 0;
    private static final int min = 0;
    private static final int max = 100;
    private static final int heatBarLength = 10;

    public void reset() {
        value = 0;
    }

    public boolean add() {
        if (value + 1 > max)
            return false;
        else {
            value++;
            return true;
        }
    }

    /**
     * 現在の蓄熱状況を文字列として取得する
     * @return 蓄熱状況
     */
    public TextComponent getHeatAsString() {
        if(value == max){
            return Component.text("水バケツで右クリック").color(NamedTextColor.RED);
        }
        
        int stage = value / ((max - min) / heatBarLength);
        TextComponent.Builder heatBar = Component.text();
        for (int i = 0; i < stage; i++) {
            heatBar.append(Component.text("|").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true));
        }
        for (int i = 0; i < heatBarLength - stage; i++) {
            heatBar.append(Component.text("|").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true));
        }
        return heatBar.build();
    }
}
