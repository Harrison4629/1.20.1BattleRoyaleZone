package net.harrison.battleroyalezone.manager;

import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.concurrent.ThreadLocalRandom;

public class ZoneManager {
    private final MinecraftServer serverInstance;
    private final Level level;

    private Vec3 previousZoneCenter;
    private Vec3 nextZoneCenter;
    private int stage;
    private boolean isRunning = false;

    private int leftTicks;

    private ZoneStateEnum currentState = ZoneStateEnum.IDLE;

    private static final int TICKS_PER_SECOND = 20;
    private static final int MIN_IDLE_SECONDS = 11;
    private static final int MAX_IDLE_SECONDS = 20;

    public ZoneManager(MinecraftServer server, Level level) {
        this.serverInstance = server;
        this.level = level;
    }

    public void tick() {
        if (!isRunning) {
            return;
        }

        if (stage >= ZoneData.getMaxStage()) {
            return;
        }

        if (leftTicks > 0) {
            leftTicks--;
        } else {
            advanceState(currentState);
        }
    }

    private void advanceState(ZoneStateEnum currentState) {
        this.currentState = switch (currentState) {
            case IDLE -> ZoneStateEnum.WARNING;
            case WARNING -> ZoneStateEnum.SHRINKING;
            case SHRINKING -> ZoneStateEnum.IDLE;
        };

        switch (this.currentState) {
            case IDLE -> onEnterIDLE();
            case WARNING -> onEnterWARNING();
            case SHRINKING -> onEnterSHRINKING();
        }
    }

    private void onEnterIDLE() {
        stage++;
        this.previousZoneCenter = this.nextZoneCenter;
        calculateNextCenter();
        int idleDurationTicks = ThreadLocalRandom.current().nextInt(MIN_IDLE_SECONDS, MAX_IDLE_SECONDS + 1) * TICKS_PER_SECOND;
        leftTicks = idleDurationTicks;

        if (stage >= ZoneData.getMaxStage()) {
            handleFinalZone();
        } else {
            postEvent(idleDurationTicks);
        }
    }

    private void onEnterWARNING() {
        int warningDurationTicks = ZoneData.getWarningTick(stage);
        leftTicks = warningDurationTicks;
        postEvent(warningDurationTicks);
    }

    private void onEnterSHRINKING() {
        int shrinkingDurationTicks = ZoneData.getShrinkTick(stage);
        leftTicks = shrinkingDurationTicks;
        postEvent(shrinkingDurationTicks);
    }

    public void start(Vec3 startPosition) {
        this.stage = 0;
        this.previousZoneCenter = startPosition;
        this.nextZoneCenter = startPosition;
        this.currentState = ZoneStateEnum.IDLE;

        calculateNextCenter();
        int idleDurationTicks = ThreadLocalRandom.current().nextInt(MIN_IDLE_SECONDS, MAX_IDLE_SECONDS + 1) * TICKS_PER_SECOND;
        leftTicks = idleDurationTicks;
        this.isRunning = true;

        postEvent(idleDurationTicks);
    }

    public void stop() {
        isRunning = false;
        handleFinalZone();
    }

    private void handleFinalZone() {
        currentState = ZoneStateEnum.IDLE;
        stage = ZoneData.getMaxStage();
        postEvent(0);
    }

    private void calculateNextCenter() {
        if (stage >= ZoneData.getMaxStage()) {
            // 如果下一阶段就是最终阶段或超出，中心不移动
            nextZoneCenter = previousZoneCenter;
            return;
        }

        double previousSize = ZoneData.getZoneSize(stage - 1);
        double nextSize = ZoneData.getZoneSize(stage);
        double offsetRadius = (previousSize - nextSize) / 2.0;

        double offsetX = ThreadLocalRandom.current().nextDouble(previousZoneCenter.x - offsetRadius, previousZoneCenter.x + offsetRadius);
        double offsetZ = ThreadLocalRandom.current().nextDouble(previousZoneCenter.z - offsetRadius, previousZoneCenter.z + offsetRadius);

        this.nextZoneCenter = new Vec3(offsetX, previousZoneCenter.y(), offsetZ);
    }

    private void postEvent(int stateDurationTicks) {
        MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, level, isRunning, previousZoneCenter, nextZoneCenter, stage, currentState, stateDurationTicks));
    }
}
