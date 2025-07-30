package net.harrison.battleroyalezone.manager;

import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.minecraft.util.Mth;
import org.joml.Vector2d;

public class LerpManager {

    private final double previousZoneLength;
    private final double nextZoneLength;
    private final Vector2d previousZoneCenter;
    private final Vector2d nextZoneCenter;
    private final int totalTicks;
    private final boolean isShrinking;

    private double currentZoneSize;
    private Vector2d currentZoneCenter;
    private int leftTicks;

    public LerpManager(double previousZoneLength, double nextZoneLength, Vector2d previousZoneCenter, Vector2d nextZoneCenter, int totalTicks, boolean isShrinking) {
        this.previousZoneLength = previousZoneLength;
        this.nextZoneLength = nextZoneLength;
        this.previousZoneCenter = previousZoneCenter;
        this.nextZoneCenter = nextZoneCenter;
        this.totalTicks = totalTicks;
        this.leftTicks = totalTicks;
        this.isShrinking = isShrinking;
        this.currentZoneCenter = previousZoneCenter;
    }

    public static LerpManager fromZoneEvent(ZoneStageEvent event) {
        return new LerpManager(
                event.getPreviousZoneSize(),
                event.getFutureZoneSize(),
                new Vector2d( event.getPreviousZoneCenter().x,  event.getPreviousZoneCenter().z),
                new Vector2d( event.getNextZoneCenter().x,  event.getNextZoneCenter().z),
                event.getStateDurationTicks(),
                event.getState() == ZoneStateEnum.SHRINKING);
    }

    public void tick() {
        if (leftTicks >=0) {
            double progress;

            if (isShrinking) {
                progress = 1 -  (double) leftTicks / totalTicks;
            } else {
                progress = 0;
            }

            currentZoneCenter = new Vector2d(
                    Mth.lerp(progress, previousZoneCenter.x, nextZoneCenter.x),
                    Mth.lerp(progress, previousZoneCenter.y, nextZoneCenter.y)

            );

            currentZoneSize = Mth.lerp(progress, previousZoneLength, nextZoneLength);

            leftTicks--;
        }
    }

    public int getLeftTicks() {
        return leftTicks;
    }

    public double getCurrentZoneSize() {
        return currentZoneSize;
    }

    public Vector2d getCurrentZoneCenter() {
        return currentZoneCenter;
    }

    public double getNextZoneLength() {
        return nextZoneLength;
    }

    public double getPreviousZoneLength() {
        return previousZoneLength;
    }

    public int getTotalTicks() {
        return totalTicks;
    }

    public Vector2d getNextZoneCenter() {
        return nextZoneCenter;
    }

    public Vector2d getPreviousZoneCenter() {
        return previousZoneCenter;
    }
}
