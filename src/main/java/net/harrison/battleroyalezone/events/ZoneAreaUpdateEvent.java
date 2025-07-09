package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneAreaUpdateEvent {

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
        event.getServer().overworld().getWorldBorder().setCenter(event.getZoneCenter().x, event.getZoneCenter().z);
        event.getServer().overworld().getWorldBorder().setSize(ZoneConfig.getZoneSize(-1));
    }

    private static void setWorldBorder(ZoneStageEvent event) {

        //double centerX = event.getOffsetCenter().x -
        //        (event.getOffsetCenter().x - event.getZoneCenter().x) * event.getStateLeftTicks() / ZoneConfig.getShrinkTick(event.getStage());
        //double centerZ = event.getOffsetCenter().z -
        //        (event.getOffsetCenter().z - event.getZoneCenter().z) * event.getStateLeftTicks() / ZoneConfig.getShrinkTick(event.getStage());;

        event.getServer().overworld().getWorldBorder().setCenter(event.getNowCenter().x, event.getNowCenter().z);

        if (hasShrunk) {
            return;
        }

        event.getServer().overworld().getWorldBorder().lerpSizeBetween(ZoneConfig.getZoneSize(event.getStage() - 1),
                ZoneConfig.getZoneSize(Math.min(event.getStage(), ZoneConfig.getMaxStage() - 1)),
                ZoneConfig.getShrinkTick(event.getStage()) * 50L);

        hasShrunk = true;
    }
}
