package quarri6343.unredstone.impl.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import quarri6343.unredstone.UnRedstone;

import java.util.function.Consumer;

/**
 * 正の数字を受け付ける金床UIのクラス
 */
public class UINumberConfiguration {
    
    public static void openUI(Player player, Consumer<Integer> resultConsumer) {
        new AnvilGUI.Builder().onComplete((player1, s) -> onNumberInputted(player, s, resultConsumer)).text("number").title("数値設定").plugin(UnRedstone.getInstance()).open(player);
    }

    private static AnvilGUI.Response onNumberInputted(Player player, String text, Consumer<Integer> resultConsumer) {
        int result;
        try{
            result = Integer.parseInt(text);
        }
        catch (NumberFormatException e){
            player.sendMessage(Component.text("数字以外を入力しないでください").color(NamedTextColor.RED));
            return AnvilGUI.Response.close();
        }
        
        if(result <= 0){
            player.sendMessage(Component.text("正の数字を入力してください").color(NamedTextColor.RED));
            return AnvilGUI.Response.close();
        }
        
        resultConsumer.accept(result);
        player.sendMessage(Component.text("数値を" + result + "に設定しました"));
        return AnvilGUI.Response.close();
    }
}
