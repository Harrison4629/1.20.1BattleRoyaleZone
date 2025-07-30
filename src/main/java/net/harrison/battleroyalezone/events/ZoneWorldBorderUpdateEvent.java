package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.harrison.battleroyalezone.manager.LerpManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneWorldBorderUpdateEvent {
    private static Level level;
    private static boolean previousRunningState = false;
    private static LerpManager manager;

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {

        manager = LerpManager.fromZoneEvent(event);

        level = event.getLevel();

        if (event.getRunningState() && !previousRunningState) {
            level.getWorldBorder().setSize(ZoneData.getZoneSize(-1));
            level.getWorldBorder().setCenter(event.getPreviousZoneCenter().x, event.getPreviousZoneCenter().z);
        }
        previousRunningState = event.getRunningState();

        if (!event.getRunningState()) {
            level.getWorldBorder().setSize(Level.MAX_LEVEL_SIZE);
            manager = null;
            return;
        }

        if (event.getState() == ZoneStateEnum.SHRINKING) {

            level.getWorldBorder().lerpSizeBetween(
                    ZoneData.getZoneSize(event.getStage() - 1),
                    ZoneData.getZoneSize(Math.min(event.getStage(), ZoneData.getMaxStage() - 1)),
                    ZoneData.getShrinkTick(event.getStage()) * 50L);

        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (manager != null) {
                level.getWorldBorder().setCenter(manager.getCurrentZoneCenter().x, manager.getCurrentZoneCenter().y);
                manager.tick();
            }
        }
    }
}
