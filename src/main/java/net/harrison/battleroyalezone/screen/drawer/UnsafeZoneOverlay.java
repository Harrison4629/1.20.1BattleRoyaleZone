package net.harrison.battleroyalezone.screen.drawer;

import net.harrison.battleroyalezone.data.ClientMapData;
import net.harrison.battleroyalezone.screen.MapScreen;
import net.harrison.battleroyalezone.api.IPixelSpaceOverlay;
import net.harrison.battleroyalezone.util.RGBBlender;

public class UnsafeZoneOverlay implements IPixelSpaceOverlay {
    private static final int GREEN = 0x8264A032;
    @Override
    public int draw(int iniColor, int px, int pz, double mapWorldX, double mapWorldZ) {

        double CenterWorldX = ClientMapData.getCurrentZoneCenter().x;
        double CenterWorldZ = ClientMapData.getCurrentZoneCenter().y;
        double worldRadius = ClientMapData.getCurrentZoneSize() / 2;

        int pixelMinX = (int) Math.floor((CenterWorldX - worldRadius - mapWorldX) / ClientMapData.getScale() + MapScreen.Half_Map_TEXTURE_SIZE);
        int pixelMaxX = (int) Math.floor((CenterWorldX + worldRadius - mapWorldX) / ClientMapData.getScale() + MapScreen.Half_Map_TEXTURE_SIZE);
        int pixelMinZ = (int) Math.floor((CenterWorldZ - worldRadius - mapWorldZ) / ClientMapData.getScale() + MapScreen.Half_Map_TEXTURE_SIZE);
        int pixelMaxZ = (int) Math.floor((CenterWorldZ + worldRadius - mapWorldZ) / ClientMapData.getScale() + MapScreen.Half_Map_TEXTURE_SIZE);

        if (px <= pixelMinX || px >= pixelMaxX || pz <= pixelMinZ || pz >= pixelMaxZ) {
            return RGBBlender.blendColors(GREEN, iniColor);
        }
        return iniColor;
    }
}
