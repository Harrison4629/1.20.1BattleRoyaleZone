package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScoreBoardUpdateEvent {

    private static final String SHRINK = "§cShrinking";
    private static final String WARNING = "§bWill_shrink_in";
    private static final String decorate = "----------";

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {

        if (!(event.getStateLeftTicks() % 20 == 0)) {
            return;
        }
        if (event.getServer() == null) {
            return;
        }

        if (!event.getRunningState()) {
            resetScore(event);
            return;
        }

        Scoreboard scoreboard = event.getServer().getScoreboard();

        Objective objective = scoreboard.getObjective(Battleroyalezone.SCOREBOARD_OBJECTIVE_NAME);

        if (event.getState() == ZoneStateEnum.IDLE && event.getStage() == ZoneData.getMaxStage()) {
            clearScore(scoreboard, objective);
            return;
        }

        if (objective == null) {
            return;
        }

        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective);

        Score decorate = scoreboard.getOrCreatePlayerScore(ScoreBoardUpdateEvent.decorate, objective);
        decorate.setScore(0);

        ZoneStateEnum state =  event.getState();

        int Seconds = event.getStateLeftTicks() / 20;
        switch (state) {
            case IDLE :
                updateIDLEScoreBoard(scoreboard, objective);
                break;

            case WARNING:
                updateScoreBoard(scoreboard, objective, WARNING, Seconds);
                break;

            case SHRINKING:
                updateScoreBoard(scoreboard, objective, SHRINK, Seconds);
                break;

            default:
                break;
        }
    }

    private static void clearScore(Scoreboard scoreboard, Objective objective) {
        scoreboard.resetPlayerScore(SHRINK, objective);
        scoreboard.resetPlayerScore(WARNING, objective);
    }

    private static void resetScore(ZoneStageEvent event) {
        Scoreboard scoreboard = event.getServer().getScoreboard();
        Objective objective = scoreboard.getObjective(Battleroyalezone.SCOREBOARD_OBJECTIVE_NAME);
        clearScore(scoreboard, objective);
        scoreboard.resetPlayerScore(decorate, objective);
    }

    private static void updateIDLEScoreBoard(Scoreboard scoreboard, Objective objective) {
        scoreboard.resetPlayerScore(SHRINK, objective);
        scoreboard.resetPlayerScore(WARNING, objective);
    }

    private static void updateScoreBoard(Scoreboard scoreboard, Objective objective, String Display, int seconds) {
        if (Objects.equals(Display, SHRINK)) {
            scoreboard.resetPlayerScore(WARNING, objective);
        } else {
            scoreboard.resetPlayerScore(SHRINK, objective);
        }

        Score score = scoreboard.getOrCreatePlayerScore(Display, objective);
        score.setScore(seconds);
    }
}
