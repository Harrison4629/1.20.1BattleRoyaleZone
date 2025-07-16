package net.harrison.battleroyalezone.events.customEvents;

import net.harrison.battleroyalezone.config.ZoneConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

public class ZoneStageEvent extends Event {
    private final MinecraftServer server;
    private final Vec3 zoneCenter;
    private final ZoneStateEnum state;
    private final int stage;
    private final int stateLeftTicks;
    private final boolean running;
    private final Vec3 offsetCenter;

    public ZoneStageEvent(MinecraftServer server, boolean running, Vec3 zoneCenter,
                          Vec3 offsetCenter, int stage, ZoneStateEnum state, int stateLeftTicks) {
        this.running = running;
        this.zoneCenter = zoneCenter;
        this.offsetCenter = offsetCenter;
        this.stage = stage;
        this.state = state;
        this.stateLeftTicks = stateLeftTicks;
        this.server = server;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public Vec3 getZoneCenter() {
        return this.zoneCenter;
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

    public Vec3 getOffsetCenter() {
        return this.offsetCenter;
    }

    public Vec3 getCurrentCenter() {
        if (state == ZoneStateEnum.IDLE || state == ZoneStateEnum.WARNING) {
            return getZoneCenter();
        } else {
            return new Vec3(
                    getOffsetCenter().x - (getOffsetCenter().x - getZoneCenter().x) * getStateLeftTicks() / ZoneConfig.getShrinkTick(getStage()),
                    getZoneCenter().y,
                    getOffsetCenter().z - (getOffsetCenter().z - getZoneCenter().z) * getStateLeftTicks() / ZoneConfig.getShrinkTick(getStage())
            );
        }
    }

    public double getFutureZoneSize() {
        if (stage >= ZoneConfig.getMaxStage()) {
            return ZoneConfig.getZoneSize(ZoneConfig.getMaxStage() - 1);
        } else {
            return ZoneConfig.getZoneSize(stage);
        }
    }

    public double getCurrentZoneSize() {
        if (stage >= ZoneConfig.getMaxStage()) {
            return ZoneConfig.getZoneSize(ZoneConfig.getMaxStage() - 1);
        } else {
            switch (state) {
                case IDLE, WARNING -> {
                    return ZoneConfig.getZoneSize(stage - 1);
                }
                case SHRINKING -> {
                    return ZoneConfig.getZoneSize(stage) + (double) ((ZoneConfig.getZoneSize(stage - 1) - ZoneConfig.getZoneSize(stage))
                            * getStateLeftTicks()) / ZoneConfig.getShrinkTick(stage);
                }
                default -> throw new UnsupportedOperationException("It should not happened!");
            }
        }
    }
}
