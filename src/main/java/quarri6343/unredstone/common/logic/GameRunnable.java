package quarri6343.unredstone.common.logic;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import quarri6343.unredstone.UnRedstone;
import quarri6343.unredstone.common.GlobalTeamHandler;
import quarri6343.unredstone.common.data.URData;
import quarri6343.unredstone.common.data.URTeam;
import quarri6343.unredstone.utils.UnRedstoneUtils;

import java.util.function.Consumer;

import static quarri6343.unredstone.common.data.URData.*;

/**
 * 定期的に起動してゲームの状態を監視するrunnable
 */
public class GameRunnable extends BukkitRunnable {

    private int count = 0;
    private final Consumer<URTeam> onGameSuccess;

    public GameRunnable(Consumer<URTeam> onGameSuccess) {
        this.onGameSuccess = onGameSuccess;
    }

    @Override
    public void run() {
        count++;

        for (int i = 0; i < getData().teams.getTeamsLength(); i++) {
            URTeam team = getData().teams.getTeam(i);
            if (team.locomotive == null)
                continue;

            if (team.locomotive.entity.getLocation().distance(team.getEndLocation().clone().add(0, 1, 0)) < 1) {
                onGameSuccess.accept(team);
                return;
            }

            if (count % checkInventoryInterval == 0) {
                int maxHoldableItems = getData().maxHoldableItems.get();

                team.locomotive.dropExcessiveItems(Material.RAIL, maxHoldableItems);
                for (Material wood : UnRedstoneUtils.woods) {
                    team.locomotive.dropExcessiveItems(wood, maxHoldableItems);
                }
                team.locomotive.dropExcessiveItems(Material.COBBLESTONE, maxHoldableItems);
            }

            if (count % craftRailInterval == 0) {
                team.locomotive.processCrafting(getData());
            }

            if (count % heatLocomotiveInterval == 0) {
                team.locomotive.addHeat();
            }
            
            if(count % headLightInterval == 0){
                GlobalTeamHandler.giveHeadLight(headLightInterval);
            }
        }

        if (count % checkInventoryInterval == 0) {
            int maxHoldableItems = getData().maxHoldableItems.get();

            GlobalTeamHandler.dropExcessiveItems(Material.RAIL, maxHoldableItems);
            for (Material wood : UnRedstoneUtils.woods) {
                GlobalTeamHandler.dropExcessiveItems(wood, maxHoldableItems);
            }
            GlobalTeamHandler.dropExcessiveItems(Material.COBBLESTONE, maxHoldableItems);
        }

        if (count % giveBuffInterval == 0 && getData().buffStrength.get() > 0) {
            GlobalTeamHandler.giveEffectToPlayers(PotionEffectType.SPEED, giveBuffInterval * 2, getData().buffStrength.get());
            GlobalTeamHandler.giveEffectToPlayers(PotionEffectType.FAST_DIGGING, giveBuffInterval * 2, getData().buffStrength.get());
        }
    }

    private URData getData() {
        return UnRedstone.getInstance().getData();
    }
}
