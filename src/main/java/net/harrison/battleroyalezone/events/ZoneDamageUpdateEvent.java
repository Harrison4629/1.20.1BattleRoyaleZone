package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneDamageUpdateEvent {

    private static boolean previousRunningState = false;

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {

        Level level = event.getLevel();

        if (event.getRunningState() && !previousRunningState) {
            level.getWorldBorder().setDamagePerBlock(ZoneData.getZoneDamage(-1));
        }
        previousRunningState = event.getRunningState();

        if (!event.getRunningState()){
            return;
        }

        if (event.getState() == ZoneStateEnum.SHRINKING) {
            level.getWorldBorder().setDamagePerBlock(ZoneData.getZoneDamage(event.getStage()));
        }
    }

}
