package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.manager.ZoneManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneTicker {

    private static MinecraftServer server;

    private static ZoneManager manager;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        server = event.getServer();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START || event.side == LogicalSide.CLIENT) {
            return;
        }
        if (manager == null) {
            return;
        }
        manager.tick();
    }

    public static void startZoneSystem(CommandSourceStack source) {
        if (manager == null) {
            manager = new ZoneManager(server, source.getLevel());
            manager.start(source.getPosition());
        } else {
            source.sendFailure(Component.translatable("manager.battleroyalezone.start_failed").withStyle(ChatFormatting.RED));
        }
    }

    public static void stopZoneSystem() {
        if (manager != null) {
            manager.stop();
            manager = null;
        }
    }
}
