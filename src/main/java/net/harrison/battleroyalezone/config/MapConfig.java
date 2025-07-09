package net.harrison.battleroyalezone.config;

import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

public class MapConfig {

    public static Vec3 zoneCenter = Vec3.ZERO;
    public static int zoneLength = 0;

    public static int getMidX(MapItemSavedData pData) {
        int scaleDivisor = 1 << pData.scale;
        return (int) ((zoneCenter.x - pData.centerX) / scaleDivisor) + MapItem.IMAGE_WIDTH / 2;
    }

    public static int getMidZ(MapItemSavedData pData) {
        int scaleDivisor = 1 << pData.scale;
        return (int) ((zoneCenter.z - pData.centerZ) / scaleDivisor) + MapItem.IMAGE_HEIGHT / 2;
    }

    public static int getSquareSideLength(MapItemSavedData pData) {
        int scaleDivisor = 1 << pData.scale;
        return zoneLength / scaleDivisor;
    }
}
