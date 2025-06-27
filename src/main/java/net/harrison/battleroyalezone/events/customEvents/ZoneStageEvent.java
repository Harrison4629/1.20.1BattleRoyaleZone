package net.harrison.battleroyalezone.events.customEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

public class ZoneStageEvent extends Event {
    private final MinecraftServer server;
    private final Vec3 zoneCenter;
    private final ZoneStateEnum state;
    private final int stage;
    private final int StateLeftTicks;
    private final boolean running;

    public ZoneStageEvent(MinecraftServer server, boolean running, Vec3 zoneCenter,
                          int stage, ZoneStateEnum state, int StateLeftTicks) {
        this.running = running;
        this.zoneCenter = zoneCenter;
        this.stage = stage;
        this.state = state;
        this.StateLeftTicks = StateLeftTicks;
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
        return this.StateLeftTicks;
    }

    public boolean getRunningState() {
        return this.running;
    }
}
