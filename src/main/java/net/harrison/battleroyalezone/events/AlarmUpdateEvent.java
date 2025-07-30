package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AlarmUpdateEvent {

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {
        ZoneStateEnum state =  event.getState();

        if (!event.getRunningState()){
            return;
        }

        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }

        switch (state) {

            case WARNING:
                broadcastWarning(server, event.getStage());
                break;

            case SHRINKING:
                broadcastShrinking(server);
                break;
            case IDLE:

            default:
                break;
        }
    }

    private static void broadcastWarning(MinecraftServer server, int stage) {
        server.getPlayerList().broadcastSystemMessage(Component.translatable("message.battleroyalezone.zone_will_shrink_into",
                ZoneData.getWarningTick(stage) / 20, ZoneData.getZoneSize(stage)).withStyle(ChatFormatting.GOLD),
                false);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }

    private static void broadcastShrinking(MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(Component.translatable("message.battleroyalezone.zone_shrinking")
                .withStyle(ChatFormatting.RED), false);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.playNotifySound(SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
