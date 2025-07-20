package net.harrison.battleroyalezone.events.customEvents;

import net.harrison.battleroyalezone.data.ZoneData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

public class ZoneStageEvent extends Event {
    private final MinecraftServer server;
    private final Vec3 previousZoneCenter;
    private final Vec3 nextZoneCenter;
    private final ZoneStateEnum state;
    private final int stage;
    private final int stateLeftTicks;
    private final boolean running;

    public ZoneStageEvent(MinecraftServer server, boolean running, Vec3 previousZoneCenter,
                          Vec3 nextZoneCenter, int stage, ZoneStateEnum state, int stateLeftTicks) {
        this.running = running;
        this.previousZoneCenter = previousZoneCenter;
        this.nextZoneCenter = nextZoneCenter;
        this.stage = stage;
        this.state = state;
        this.stateLeftTicks = stateLeftTicks;
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public Vec3 getPreviousZoneCenter() {
        return this.previousZoneCenter;
    }

    public ZoneStateEnum getState() {
        return this.state;
    }

    public int getStage() {
        return this.stage;
    }

    public int getStateLeftTicks() {
        return this.stateLeftTicks;
    }

    public boolean getRunningState() {
        return this.running;
    }

    public Vec3 getNextZoneCenter() {
        return this.nextZoneCenter;
    }

    public Vec3 getCurrentCenter() {
        if (state == ZoneStateEnum.IDLE || state == ZoneStateEnum.WARNING) {
            return previousZoneCenter;
        } else {
            return new Vec3(
                    interpolate(previousZoneCenter.x, nextZoneCenter.x, stateLeftTicks, ZoneData.getShrinkTick(stage)),
                    previousZoneCenter.y,
                    interpolate(previousZoneCenter.z, nextZoneCenter.z, stateLeftTicks, ZoneData.getShrinkTick(stage)));
        }
    }

    public double getFutureZoneSize() {
        if (stage >= ZoneData.getMaxStage()) {
            return ZoneData.getZoneSize(ZoneData.getMaxStage() - 1);
        } else {
            return ZoneData.getZoneSize(stage);
        }
    }

    public double getCurrentZoneSize() {
        if (stage >= ZoneData.getMaxStage()) {
            return ZoneData.getZoneSize(ZoneData.getMaxStage() - 1);
        } else {
            switch (state) {
                case IDLE, WARNING -> {
                    return ZoneData.getZoneSize(stage - 1);
                }
                case SHRINKING -> {
                    return interpolate(ZoneData.getZoneSize(stage - 1), ZoneData.getZoneSize(stage), stateLeftTicks, ZoneData.getShrinkTick(stage));
                }
                default -> throw new UnsupportedOperationException("It should not happened!");
            }
        }
    }

    private double interpolate(double start, double end, int ticksLeft, int totalTicks) {
        if (totalTicks <= 0) {
            return end;
        }
        double t = (double) ticksLeft / totalTicks;
        return start * t + end * (1.0 - t);
    }
}
