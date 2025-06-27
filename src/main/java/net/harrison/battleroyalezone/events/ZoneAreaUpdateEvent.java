package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneAreaUpdateEvent {

    private static boolean hasShrinked = false;

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {
        if (event.getServer() == null) {
            return;
        }

        ZoneStateEnum state =  event.getState();
        double centerX = event.getZoneCenter().x;
        double centerZ = event.getZoneCenter().z;

        if (!event.getRunningState()){
            resetWorldBorder(event);
            return;
        }

        switch (state) {

            case IDLE:
                hasShrinked = false;
                break;

            case WARNING:
                handleInitWorldBorder(centerX, centerZ, event);
                break;

            case SHRINKING:
                handleWorldBorder(centerX, centerZ, event);
                break;

            default:
                break;
        }
    }

    private static void resetWorldBorder(ZoneStageEvent event) {
        event.getServer().overworld().getWorldBorder().setSize(59999968);
    }

    private static void handleInitWorldBorder(double centerX, double centerZ, ZoneStageEvent event) {

        if (event.getStage() != 0) {
            return;
        }
        event.getServer().overworld().getWorldBorder().setCenter(centerX, centerZ);
        event.getServer().overworld().getWorldBorder().setSize(ZoneConfig.getIniZoneSize());
    }

    private static void handleWorldBorder(double centerX, double centerZ, ZoneStageEvent event) {
        if (hasShrinked) {
            return;
        }

        event.getServer().overworld().getWorldBorder().setCenter(centerX, centerZ);

        if (event.getStage() == 0) {
            event.getServer().overworld().getWorldBorder().lerpSizeBetween(ZoneConfig.getIniZoneSize(),
                    ZoneConfig.getZoneSize(0),
                    ZoneConfig.getShrinkTick(event.getStage()) * 50L);
        } else {
            event.getServer().overworld().getWorldBorder().lerpSizeBetween(ZoneConfig.getZoneSize(event.getStage() - 1),
                    ZoneConfig.getZoneSize(Math.min(event.getStage(), ZoneConfig.getMaxStage() - 1)),
                    ZoneConfig.getShrinkTick(event.getStage()) * 50L);
        }
        hasShrinked = true;
    }
}
