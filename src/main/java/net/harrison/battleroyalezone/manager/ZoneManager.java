package net.harrison.battleroyalezone.manager;

import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.concurrent.ThreadLocalRandom;

public class ZoneManager {
    private final MinecraftServer serverInstance;

    private Vec3 previousZoneCenter;
    private Vec3 nextZoneCenter;
    private int stage;
    private boolean isRunning = false;
    private boolean hasCalculatedNextCenter = false;

    private int idleLeftTicks;
    private int warningLeftTicks;
    private int shrinkingLeftTicks;

    private ZoneStateEnum currentState = ZoneStateEnum.IDLE;

    private static final int TICKS_PER_SECOND = 20;
    private static final int MIN_IDLE_SECONDS = 11;
    private static final int MAX_IDLE_SECONDS = 20;

    public ZoneManager(MinecraftServer server) {
        this.serverInstance = server;
    }

    public void tick() {
        if (!isRunning) {
            return;
        }

        if (stage < ZoneData.getMaxStage()) {
            switch (currentState) {
                case IDLE -> handleIdleTick();
                case WARNING -> handleWarningTick();
                case SHRINKING -> handleShrinkingTick();
            }
        }
    }

    private void handleIdleTick() {
        if (idleLeftTicks > 0) {
            if (!hasCalculatedNextCenter) {
                calculateNextCenter();
                hasCalculatedNextCenter = true;
            }
            postEvent(idleLeftTicks);
            idleLeftTicks--;
        } else {
            currentState = ZoneStateEnum.WARNING;
            warningLeftTicks = ZoneData.getWarningTick(stage);
            postEvent(warningLeftTicks);
        }
    }

    private void handleWarningTick() {
        if (warningLeftTicks > 0) {
            postEvent(warningLeftTicks);
            warningLeftTicks--;
        } else {
            currentState = ZoneStateEnum.SHRINKING;
            shrinkingLeftTicks = ZoneData.getShrinkTick(stage);
            postEvent(shrinkingLeftTicks);
        }
    }

    private void handleShrinkingTick() {
        if (shrinkingLeftTicks > 0) {
            postEvent(shrinkingLeftTicks);
            shrinkingLeftTicks--;
        } else {
            stage++;
            previousZoneCenter = nextZoneCenter;
            hasCalculatedNextCenter = false;

            if (stage >= ZoneData.getMaxStage()) {
                handleFinalZone();
            } else {
                currentState = ZoneStateEnum.IDLE;
                int idleSeconds = ThreadLocalRandom.current().nextInt(MIN_IDLE_SECONDS, MAX_IDLE_SECONDS + 1);
                idleLeftTicks = idleSeconds * TICKS_PER_SECOND;
                postEvent(idleLeftTicks);
            }
        }
    }

    public void start(Vec3 startPosition) {
        if (isRunning) return;

        this.stage = 0;
        this.previousZoneCenter = startPosition;
        this.nextZoneCenter = startPosition;
        this.currentState = ZoneStateEnum.IDLE;

        int idleSeconds = ThreadLocalRandom.current().nextInt(MIN_IDLE_SECONDS, MAX_IDLE_SECONDS + 1);
        this.idleLeftTicks = idleSeconds * TICKS_PER_SECOND;

        this.isRunning = true;
        postEvent(idleLeftTicks);
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

        double currentSize = ZoneData.getZoneSize(stage - 1);
        double nextSize = ZoneData.getZoneSize(stage);
        double offsetRadius = (currentSize - nextSize) / 2.0;

        double offsetX = ThreadLocalRandom.current().nextDouble(previousZoneCenter.x - offsetRadius, previousZoneCenter.x + offsetRadius);
        double offsetZ = ThreadLocalRandom.current().nextDouble(previousZoneCenter.z - offsetRadius, previousZoneCenter.z + offsetRadius);

        //double offsetZ = RandomNumSummoner.randomDoubleBetween(previousZoneCenter.x() - offsetRadius, previousZoneCenter.x() + offsetRadius);
        //double offsetZ = RandomNumSummoner.randomDoubleBetween(previousZoneCenter.z() - offsetRadius, previousZoneCenter.z() + offsetRadius);

        this.nextZoneCenter = new Vec3(offsetX, previousZoneCenter.y(), offsetZ);
    }

    private void postEvent(int ticksLeft) {
        MinecraftForge.EVENT_BUS.post(new ZoneStageEvent(serverInstance, isRunning, previousZoneCenter, nextZoneCenter, stage, currentState, ticksLeft));
    }
}
