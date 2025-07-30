package net.harrison.battleroyalezone.screen.drawer;

import net.harrison.battleroyalezone.api.IPixelSpaceOverlay;
import net.harrison.battleroyalezone.util.RGBBlender;

public class CircleAreaOverlay implements IPixelSpaceOverlay {
    private static final int RED = 0xB4FF2800;
    private final double radius;
    private final double worldX;
    private final double worldZ;

    public CircleAreaOverlay(double radius, double worldX, double worldZ) {
        this.radius = radius;
        this.worldX = worldX;
        this.worldZ = worldZ;
    }

    @Override
    public int draw(int iniColor, int px, int pz, double mapWorldX, double mapWorldZ) {
        if ((mapWorldX - worldX) * (mapWorldX - worldX) + (mapWorldZ - worldZ) * (mapWorldZ - worldZ) < radius) {
            return RGBBlender.blendColors(RED, iniColor);
        }
        return iniColor;
    }
}
