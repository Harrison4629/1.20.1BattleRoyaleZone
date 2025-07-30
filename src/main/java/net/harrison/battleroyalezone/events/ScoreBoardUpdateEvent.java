package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.harrison.battleroyalezone.manager.LerpManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScoreBoardUpdateEvent {

    private static final String SHRINK = "§cShrinking";
    private static final String WARNING = "§eWill_shrink_in";
    private static final String IDLE = "§bIDLE";
    private static final String decorate = "----------";
    private static LerpManager manager;
    private static Level level;

    private static Objective objective;

    private static String CURRENT_STATE;
    private static String PREVIOUS_STATE;


    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {

        manager = LerpManager.fromZoneEvent(event);

        level = event.getLevel();

        if (!event.getRunningState()) {
            manager = null;
            resetScore(level);
            return;
        }



        Scoreboard scoreboard = event.getLevel().getScoreboard();

        objective = scoreboard.getObjective(Battleroyalezone.SCOREBOARD_OBJECTIVE_NAME);

        if (event.getState() == ZoneStateEnum.IDLE && event.getStage() == ZoneData.getMaxStage()) {
            clearScore(event.getLevel().getScoreboard(), objective);
            return;
        }

        scoreboard.setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective);

        scoreboard.getOrCreatePlayerScore(ScoreBoardUpdateEvent.decorate, objective).setScore(0);

        switch (event.getState()) {
            case IDLE -> CURRENT_STATE = IDLE;
            case WARNING -> CURRENT_STATE = WARNING;
            case SHRINKING -> CURRENT_STATE = SHRINK;
        }
    }

    private static void clearScore(Scoreboard scoreboard, Objective objective) {
        scoreboard.resetPlayerScore(SHRINK, objective);
        scoreboard.resetPlayerScore(WARNING, objective);
        scoreboard.resetPlayerScore(IDLE, objective);
    }

    private static void resetScore(Level level) {
        Scoreboard scoreboard = level.getScoreboard();
        Objective objective = scoreboard.getObjective(Battleroyalezone.SCOREBOARD_OBJECTIVE_NAME);

        clearScore(scoreboard, objective);
        scoreboard.resetPlayerScore(decorate, objective);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (manager != null) {

                if (manager.getLeftTicks() % 20 == 0) {
                    level.getScoreboard().getOrCreatePlayerScore(CURRENT_STATE, objective).
                            setScore(manager.getLeftTicks() / 20);

                    if (!Objects.equals(CURRENT_STATE, PREVIOUS_STATE)) {
                        clearScore(level.getScoreboard(), objective);
                        PREVIOUS_STATE = CURRENT_STATE;
                    }
                }
                manager.tick();
            }
        }
    }
}
