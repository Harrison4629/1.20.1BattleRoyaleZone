package net.harrison.battleroyalezone.events.customEvents;

import net.harrison.battleroyalezone.data.ZoneData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

public class ZoneStageEvent extends Event {
    private final MinecraftServer server;
    private final Level level;
    private final Vec3 previousZoneCenter;
    private final Vec3 nextZoneCenter;
    private final ZoneStateEnum state;
    private final int stage;
    private final int stateDurationTicks;
    private final boolean running;

    public ZoneStageEvent(MinecraftServer server, Level level, boolean running, Vec3 previousZoneCenter,
                          Vec3 nextZoneCenter, int stage, ZoneStateEnum state, int stateDurationTicks) {
        this.running = running;
        this.previousZoneCenter = previousZoneCenter;
        this.nextZoneCenter = nextZoneCenter;
        this.stage = stage;
        this.state = state;
        this.stateDurationTicks = stateDurationTicks;
        this.server = server;
        this.level = level;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public Level getLevel() {
        return this.level;
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

    public int getStateDurationTicks() {
        return this.stateDurationTicks;
    }

    public boolean getRunningState() {
        return this.running;
    }

    public Vec3 getNextZoneCenter() {
        return this.nextZoneCenter;
    }

    public double getPreviousZoneSize() {
        return ZoneData.getZoneSize(stage - 1);
    }

    public double getFutureZoneSize() {
        if (stage >= ZoneData.getMaxStage()) {
            return ZoneData.getZoneSize(ZoneData.getMaxStage() - 1);
        } else {
            return ZoneData.getZoneSize(stage);
        }
    }

    public boolean isFinalZone() {
        return stage == ZoneData.getMaxStage();
    }
}
