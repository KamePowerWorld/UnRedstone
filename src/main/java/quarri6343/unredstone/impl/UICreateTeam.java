package quarri6343.unredstone.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.UnRedstoneData;

public class UICreateTeam {

    private static String inputtedTeamName = "";
    private static String inputtedTeamColor = "";
    
    public static void openUI(Player player) {
        new AnvilGUI.Builder().onComplete(UICreateTeam::onTeamNameInputted).text("name").title("チームの名前を入力").plugin(UnRedstone.getInstance()).open(player);
    }

    private static AnvilGUI.Response onTeamNameInputted(Player player, String text){
        UnRedstoneData data = UnRedstone.getInstance().data;
        if(data.getTeambyName(text) != null){
            player.sendMessage(Component.text("その名前のチームは既に存在します").color(NamedTextColor.RED));
            return AnvilGUI.Response.close();
        }

        inputtedTeamName = text;
        openColorUI(player);
        return AnvilGUI.Response.close();
    }
    
    private static void openColorUI(Player player){
        new AnvilGUI.Builder().onComplete(UICreateTeam::onTeamColorInputted).text("color").title("チームの色を入力。例：red").plugin(UnRedstone.getInstance()).open(player);
    }
    
    private static AnvilGUI.Response onTeamColorInputted(Player player, String text){
        if(NamedTextColor.NAMES.value(text) == null){
            player.sendMessage(Component.text("チームカラーが不正です。redやgreenのように半角小文字で指定してください").color(NamedTextColor.RED));
            return AnvilGUI.Response.close();
        }

        UnRedstoneData data = UnRedstone.getInstance().data;
        if(data.getTeambyColor(text) != null){
            player.sendMessage(Component.text("その色のチームは既に存在します").color(NamedTextColor.RED));
            return AnvilGUI.Response.close();
        }
        
        inputtedTeamColor = text;

        data.addTeam(inputtedTeamName, inputtedTeamColor);
        player.sendMessage(Component.text("チーム「" + inputtedTeamName + "」を作成しました").color(NamedTextColor.NAMES.value(inputtedTeamColor)));
        return AnvilGUI.Response.close();
    }
}
