package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.init.ModMessages;
import net.harrison.battleroyalezone.networking.s2cpacket.MapReferenceSyncS2CPacket;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneMapDataUpdateEvent {

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {
        if (!event.getRunningState()) {
            ModMessages.sendToAllPlayer(new MapReferenceSyncS2CPacket(
                    Vec3.ZERO,
                    Vec3.ZERO,
                    0,
                    0,
                    1.0F,
                    true
            ));
            return;
        }

        ModMessages.sendToAllPlayer(new MapReferenceSyncS2CPacket(
                event.getCurrentCenter(),
                event.getNextZoneCenter(),
                event.getCurrentZoneSize(),
                event.getFutureZoneSize(),
                event.getCurrentZoneSize() / Battleroyalezone.MAP_TEXTURE_SIZE,
                false));
    }
}
