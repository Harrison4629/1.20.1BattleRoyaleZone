package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneStagePublisherEvent {

    private static MinecraftServer serverInstance;

    private static Vec3 zoneCenter;
    private static int stage;
    private static boolean isRunning = false;

    private static int IDLELeftTicks;
    private static int WARNINGLeftTicks;
    private static int SHRINKINGLeftTicks;

    private static ZoneStateEnum currentState = ZoneStateEnum.IDLE;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.START || event.side == LogicalSide.CLIENT) {
            return;
        }

        if (!isRunning || serverInstance == null){
            return;
        }

        if (stage < ZoneConfig.getMaxStage()) {

            switch (currentState) {
                case WARNING:
                    handleWARNINGTicks(event);
                    break;
                case SHRINKING:
                    handleSHRINKINGTicks(event);
                    break;
                case IDLE:
                    handleIDLETicks(event);
                    break;
                default:
                    break;
            }
        } else {
            handleZoneStageOver();
        }
    }

    private static void handleZoneStageOver() {
        stage = ZoneConfig.getMaxStage();
        currentState = ZoneStateEnum.IDLE;
        MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, stage, currentState, 0));
    }

    private static void handleIDLETicks(TickEvent.ServerTickEvent event) {
        if (IDLELeftTicks >= 0 ) {

            if (IDLELeftTicks %20 == 0) {
                MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, stage, currentState, IDLELeftTicks));
            }

            IDLELeftTicks--;
        } else {
            currentState = ZoneStateEnum.WARNING;
            IDLELeftTicks = (int) ((Math.random() * 10) + 11) * 20;
        }
    }

    private static void handleWARNINGTicks(TickEvent.ServerTickEvent event) {
        if (WARNINGLeftTicks >= 0 ) {

            if (WARNINGLeftTicks %20 == 0) {
                MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, stage, currentState, WARNINGLeftTicks));
            }

            WARNINGLeftTicks--;
        } else {
            currentState = ZoneStateEnum.SHRINKING;
            WARNINGLeftTicks = ZoneConfig.getWarningTick(Math.min(stage + 1, ZoneConfig.getMaxStage() - 1));
        }
    }

    private static void handleSHRINKINGTicks(TickEvent.ServerTickEvent event) {
        if (SHRINKINGLeftTicks >= 0) {

            if (SHRINKINGLeftTicks % 20 == 0) {
                MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, stage, currentState, SHRINKINGLeftTicks));
            }

            SHRINKINGLeftTicks--;
        } else {
            currentState = ZoneStateEnum.IDLE;
            //SHRINKINGLeftTicks = ZoneConfig.getShrinkTick(Math.min(stage + 1, ZoneConfig.getMaxStage() - 1));
            stage++;
            SHRINKINGLeftTicks = ZoneConfig.getShrinkTick(stage);
        }
    }

    public static void startZoneSystem(CommandSourceStack source) {

        serverInstance = source.getServer();

        zoneCenter = source.getPosition();
        stage = 0;

        WARNINGLeftTicks = ZoneConfig.getWarningTick(stage);
        SHRINKINGLeftTicks = ZoneConfig.getShrinkTick(stage);

        currentState = ZoneStateEnum.WARNING;
        isRunning = true;
    }

    public static void stopZoneSystem() {
        handleZoneStageOver();
        isRunning = false;
        MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, stage, currentState, 0));
    }


}
