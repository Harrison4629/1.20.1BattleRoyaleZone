package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneSizeUpdateEvent {

    private static boolean hasShrunk = false;

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {
        if (event.getServer() == null) {
            return;
        }

        ZoneStateEnum state = event.getState();
        if (!event.getRunningState()){
            resetWorldBorder(event);
            return;
        }

        switch (state) {

            case IDLE:
                hasShrunk = false;
                setInitWorldBorder(event);
                break;

            case WARNING:
                break;

            case SHRINKING:
                setWorldBorder(event);
                break;

            default:
                break;
        }
    }

    private static void resetWorldBorder(ZoneStageEvent event) {
        event.getServer().overworld().getWorldBorder().setSize(59999968);
    }

    private static void setInitWorldBorder(ZoneStageEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        event.getServer().overworld().getWorldBorder().setCenter(event.getPreviousZoneCenter().x, event.getPreviousZoneCenter().z);
        event.getServer().overworld().getWorldBorder().setSize(ZoneData.getZoneSize(-1));
    }

    private static void setWorldBorder(ZoneStageEvent event) {
        event.getServer().overworld().getWorldBorder().setCenter(event.getCurrentCenter().x, event.getCurrentCenter().z);

        if (hasShrunk) {
            return;
        }

        event.getServer().overworld().getWorldBorder().lerpSizeBetween(ZoneData.getZoneSize(event.getStage() - 1),
                ZoneData.getZoneSize(Math.min(event.getStage(), ZoneData.getMaxStage() - 1)),
                ZoneData.getShrinkTick(event.getStage()) * 50L);

        hasShrunk = true;
    }
}
