package net.harrison.battleroyalezone;

import net.harrison.battleroyalezone.events.ZoneTicker;
import net.harrison.battleroyalezone.init.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Battleroyalezone.MODID)
public class Battleroyalezone {

    public static final String SCOREBOARD_OBJECTIVE_NAME = "BattleroyaleZone";
    private static final Component SCOREBOARD_DISPLAY_NAME = Component.translatable("socreboard.battleroyalezone.display_name").withStyle(ChatFormatting.AQUA);

    public static final String MODID = "battleroyalezone";

    public Battleroyalezone() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ModConfigs.SPEC, "Battleroyale-Zone.toml");

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();

        Scoreboard scoreboard = server.getScoreboard();
        Objective zoneObjective = scoreboard.getObjective(SCOREBOARD_OBJECTIVE_NAME);

        if (zoneObjective == null) {
            scoreboard.addObjective(
                    SCOREBOARD_OBJECTIVE_NAME,
                    ObjectiveCriteria.DUMMY,
                    SCOREBOARD_DISPLAY_NAME,
                    ObjectiveCriteria.RenderType.INTEGER
            );
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ZoneTicker.stopZoneSystem();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(ModKeyBinds.OPEN_MAP);
        }
    }
}
