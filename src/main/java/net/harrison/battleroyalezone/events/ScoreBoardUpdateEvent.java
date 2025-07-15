package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScoreBoardUpdateEvent {

    private static final String SCOREBOARD_OBJECTIVE_NAME = "BattleroyaleZone";

    private static final String SHRINK = "§cShrinking";
    private static final String WARNING = "§bWill_shrink_in";




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

        if (event.getState() == ZoneStateEnum.IDLE && event.getStage() == ZoneConfig.getMaxStage()) {
            clearScore(event);
            return;
        }


        Scoreboard scoreboard = event.getServer().getScoreboard();

        Objective objective = scoreboard.getObjective(SCOREBOARD_OBJECTIVE_NAME);

        if (objective == null) {
            return;
        }

        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective);

        Score decorate = scoreboard.getOrCreatePlayerScore("----------", objective);
        decorate.setScore(0);



        ZoneStateEnum state =  event.getState();

        int Seconds = event.getStateLeftTicks() / 20;
        switch (state) {
            case IDLE :
                handleIDLEScoreBoard(Seconds, event);
                break;

            case WARNING:
                handleWARNINGScoreBoard(Seconds, event);
                break;

            case SHRINKING:
                handleSHRINKINGScoreBoard(Seconds, event);
                break;

            default:
                break;
        }
    }

    private static void clearScore(ZoneStageEvent event) {
        Scoreboard scoreboard = event.getServer().getScoreboard();
        Objective objective = scoreboard.getObjective(SCOREBOARD_OBJECTIVE_NAME);

        scoreboard.resetPlayerScore(SHRINK, objective);
        scoreboard.resetPlayerScore(WARNING, objective);
    }

    private static void resetScore(ZoneStageEvent event) {
        clearScore(event);

        Scoreboard scoreboard = event.getServer().getScoreboard();
        Objective objective = scoreboard.getObjective(SCOREBOARD_OBJECTIVE_NAME);
        scoreboard.resetPlayerScore("----------", objective);

    }

    private static void handleIDLEScoreBoard(int seconds, ZoneStageEvent event) {
        Scoreboard scoreboard = event.getServer().getScoreboard();
        Objective objective = scoreboard.getObjective(SCOREBOARD_OBJECTIVE_NAME);
        scoreboard.resetPlayerScore(SHRINK, objective);
        scoreboard.resetPlayerScore(WARNING, objective);

    }

    private static void handleWARNINGScoreBoard(int seconds, ZoneStageEvent event) {
        Scoreboard scoreboard = event.getServer().getScoreboard();
        Objective objective = scoreboard.getObjective(SCOREBOARD_OBJECTIVE_NAME);
        scoreboard.resetPlayerScore(SHRINK, objective);

        Score score = null;
        if (objective != null) {
            score = scoreboard.getOrCreatePlayerScore(WARNING, objective);
        }

        if (score != null) {
            score.setScore(seconds);
        }
    }

    private static void handleSHRINKINGScoreBoard(int seconds, ZoneStageEvent event) {
        Scoreboard scoreboard = event.getServer().getScoreboard();
        Objective objective = scoreboard.getObjective(SCOREBOARD_OBJECTIVE_NAME);
        scoreboard.resetPlayerScore(WARNING, objective);

        Score score = null;
        if (objective != null) {
            score = scoreboard.getOrCreatePlayerScore(SHRINK, objective);
        }

        if (score != null) {
            score.setScore(seconds);
        }
    }
}
