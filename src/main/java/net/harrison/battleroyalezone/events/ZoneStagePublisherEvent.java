package net.harrison.battleroyalezone.events;

import net.harrison.basicdevtool.math.RandomNumSummoner;
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
    private static Vec3 offsetCenter;
    private static int stage;
    private static boolean isRunning = false;

    private static boolean hasNewCenter = false;

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
                case IDLE -> handleIDLETicks(event);
                case SHRINKING -> handleSHRINKINGTicks(event);
                case WARNING -> handleWARNINGTicks(event);
                default -> throw new UnsupportedOperationException("It should not happened!");
            }
        } else {
            handleFinalZone();
        }
    }


    private static void handleIDLETicks(TickEvent.ServerTickEvent event) {
        if (IDLELeftTicks >= 0 ) {
            if (!hasNewCenter) {
                summonOffestCenter(zoneCenter, stage);
                hasNewCenter = true;
            }

            MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, offsetCenter, stage, currentState, IDLELeftTicks));

            IDLELeftTicks--;
        } else {
            currentState = ZoneStateEnum.WARNING;
            IDLELeftTicks = (int) ((Math.random() * 10) + 11) * 20;
        }
    }

    private static void handleWARNINGTicks(TickEvent.ServerTickEvent event) {
        if (WARNINGLeftTicks >= 0 ) {
            hasNewCenter = false;

            MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, offsetCenter, stage, currentState, WARNINGLeftTicks));

            WARNINGLeftTicks--;
        } else {
            currentState = ZoneStateEnum.SHRINKING;
            WARNINGLeftTicks = ZoneConfig.getWarningTick(stage + 1);
        }
    }

    private static void handleSHRINKINGTicks(TickEvent.ServerTickEvent event) {
        if (SHRINKINGLeftTicks >= 0) {

            MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, offsetCenter, stage, currentState, SHRINKINGLeftTicks));

            SHRINKINGLeftTicks--;
        } else {
            currentState = ZoneStateEnum.IDLE;
            stage++;
            SHRINKINGLeftTicks = ZoneConfig.getShrinkTick(stage);
            zoneCenter = offsetCenter;
        }
    }

    public static void startZoneSystem(CommandSourceStack source) {
        serverInstance = source.getServer();

        stage = 0;

        currentState = ZoneStateEnum.IDLE;

        zoneCenter = source.getPosition();

        IDLELeftTicks = (int) ((Math.random() * 10) + 11) * 20;
        WARNINGLeftTicks = ZoneConfig.getWarningTick(stage);
        SHRINKINGLeftTicks = ZoneConfig.getShrinkTick(stage);

        isRunning = true;
    }

    public static void stopZoneSystem() {
        handleFinalZone();
        isRunning = false;
        MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, offsetCenter, stage, currentState, 0));
    }

    private static void handleFinalZone() {
        stage = ZoneConfig.getMaxStage();
        currentState = ZoneStateEnum.IDLE;
        MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, zoneCenter, offsetCenter, stage, currentState, 0));
    }

    private static void summonOffestCenter(Vec3 zoneCenter, int stage) {

        double offsetRadius = (ZoneConfig.getZoneSize(stage - 1) - ZoneConfig.getZoneSize(stage)) / 2.0;

        double offsetX =  RandomNumSummoner.randomDoubleBetween(zoneCenter.x - offsetRadius, zoneCenter.x + offsetRadius);
        double offsetZ =  RandomNumSummoner.randomDoubleBetween(zoneCenter.z - offsetRadius, zoneCenter.z + offsetRadius);

        offsetCenter = new Vec3(offsetX, zoneCenter.y, offsetZ);
    }
}
