package net.harrison.battleroyalezone.events;

import net.harrison.basicdevtool.init.ModMessages;
import net.harrison.basicdevtool.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AlarmUpdateEvent {

    private static boolean hasBroadcastedWARNING = false;
    private static boolean hasBroadcastedSHRINK = false;

    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {

        ZoneStateEnum state =  event.getState();

        if (!event.getRunningState()){
            return;
        }

        switch (state) {
            case IDLE:
                hasBroadcastedSHRINK = false;
                hasBroadcastedWARNING = false;
                break;

            case WARNING:
                broadcastWarning(event);
                break;

            case SHRINKING:
                broadcastShrinking(event);
                break;

            default:
                break;
        }
    }

    private static void broadcastWarning(ZoneStageEvent event) {
        if (hasBroadcastedWARNING) {
            return;
        }

        event.getServer().getPlayerList().broadcastSystemMessage(Component.literal( "毒圈将在" + ZoneConfig.getWarningTick(event.getStage()) / 20 + "秒后缩小至"
                + ZoneConfig.getZoneSize(event.getStage()) + "格大小"), true);
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.5F, 1.0F), player);
        }

        hasBroadcastedWARNING = true;
    }

    private static void broadcastShrinking(ZoneStageEvent event) {
        if (hasBroadcastedSHRINK) {
            return;
        }

        event.getServer().getPlayerList().broadcastSystemMessage(Component.literal("毒圈正在收缩!"), true);
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            ModMessages.sendToPlayer(new PlaySoundToClientS2CPacket(SoundEvents.BEACON_ACTIVATE, 1.5F, 1.0F), player);
        }

        hasBroadcastedSHRINK = true;
    }

}
