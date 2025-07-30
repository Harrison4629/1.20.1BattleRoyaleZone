package net.harrison.battleroyalezone.screen.drawer;

import net.harrison.battleroyalezone.data.ClientMapData;
import net.harrison.battleroyalezone.screen.MapScreen;
import net.harrison.battleroyalezone.api.IPixelSpaceOverlay;
import net.harrison.battleroyalezone.util.RGBBlender;

public class NextSafeZoneBorderOverlay implements IPixelSpaceOverlay {

    private static final int WHITE = 0xC8FFFFFF;
    private final double CenterWorldX;
    private final double CenterWorldZ;
    private final double WorldRadius;

    public NextSafeZoneBorderOverlay(double centerWorldX, double centerWorldZ, double worldRadius) {
        this.CenterWorldX = centerWorldX;
        this.CenterWorldZ = centerWorldZ;
        this.WorldRadius = worldRadius;
    }

    @Override
    public int draw(int iniColor, int px, int pz, double mapWorldX, double mapWorldZ) {

        int pixelMinX = (int) Math.floor((CenterWorldX - WorldRadius - mapWorldX) / ClientMapData.getScale() + MapScreen.Half_Map_TEXTURE_SIZE);
        int pixelMaxX = (int) Math.floor((CenterWorldX + WorldRadius - mapWorldX) / ClientMapData.getScale() + MapScreen.Half_Map_TEXTURE_SIZE);
        int pixelMinZ = (int) Math.floor((CenterWorldZ - WorldRadius - mapWorldZ) / ClientMapData.getScale() + MapScreen.Half_Map_TEXTURE_SIZE);
        int pixelMaxZ = (int) Math.floor((CenterWorldZ + WorldRadius - mapWorldZ) / ClientMapData.getScale() + MapScreen.Half_Map_TEXTURE_SIZE);

        if (((pz == pixelMaxZ || pz == pixelMinZ) && px >= pixelMinX && px <= pixelMaxX)
                || ((px == pixelMaxX || px == pixelMinX) && pz >= pixelMinZ && pz <= pixelMaxZ)) {
            return RGBBlender.blendColors(WHITE, iniColor);
        }
        return iniColor;
    }
}
