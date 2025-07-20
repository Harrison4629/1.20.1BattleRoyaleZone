package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneDamageUpdateEvent {

    private static boolean hasSet = false;

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {
        if (event.getServer() == null) {
            return;
        }

        ZoneStateEnum state = event.getState();

        if (!event.getRunningState()){
            return;
        }

        switch (state) {

            case IDLE:
                handleInitWorldBorderDamage(event);
                hasSet = false;
                break;

            case WARNING:
                break;

            case SHRINKING:
                handleWorldBorderDamage(event);
                break;

            default:
                break;
        }
    }

    private static void handleInitWorldBorderDamage(ZoneStageEvent event) {
        if (event.getStage() != 0) {
            return;
        }
        event.getServer().overworld().getWorldBorder().setDamagePerBlock(ZoneData.getZoneDamage(-1));
    }

    private static void handleWorldBorderDamage(ZoneStageEvent event) {
        if (hasSet) {
            return;
        }
        event.getServer().overworld().getWorldBorder().setDamagePerBlock(ZoneData.getZoneDamage(event.getStage()));

        hasSet = true;
    }
}
