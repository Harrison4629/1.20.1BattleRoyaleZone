package net.harrison.battleroyalezone.data;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.util.RGBBlender;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MapColor;

import java.util.HashMap;
import java.util.Map;

public class ClientMapData {
    private static final int UNSAFE_ZONE_OVERLAY_COLOR = 0x80E6D8AD;

    private static final Map<Long, Byte> iniMapData = new HashMap<>();

    private static double zoneCenterX;
    private static double zoneCenterZ;
    private static double nextZoneCenterX;
    private static double nextZoneCenterZ;
    private static double zoneLength = 0;
    private static double nextZoneLength;
    private static double scale = 1.0F;


    public static void reset() {
        zoneLength = 0;
        nextZoneLength = 0;
        zoneCenterX = 0;
        zoneCenterZ = 0;
        nextZoneCenterX = 0;
        nextZoneCenterZ = 0;
    }

    public static void receiveMapDataChunk(Map<Long, Byte> chunkData, boolean isFirstChunk) {
        if (isFirstChunk) {
            iniMapData.clear();
        }
        iniMapData.putAll(chunkData);
    }


    public static byte getBackGroundColor(int px, int pz, double playerX, double playerZ) {

        int pRadius = Battleroyalezone.MAP_TEXTURE_SIZE / 2;

        int worldX = (int) Math.floor(playerX + (px + 0.5 - pRadius) * scale);
        int worldZ = (int) Math.floor(playerZ + (pz + 0.5 - pRadius) * scale);

        if (iniMapData.containsKey(coordinatesToLong(worldX, worldZ))) {
            return iniMapData.get(coordinatesToLong(worldX, worldZ));
        } else {
            return 0;
        }
        //else if ((px + pz & 1) == 0) {
        //    return MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.NORMAL);
        //} else {
        //    return MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
        //}
    }

    public static byte drawNextSafeZone(byte iniColor, int px, int pz, double viewCenterX, double vierCenterZ) {
        if (zoneLength == 0) {
            return iniColor;
        }
        final int Half_Map_TEXTURE_SIZE = Battleroyalezone.MAP_TEXTURE_SIZE / 2;

        double nextZoneRadius = nextZoneLength / 2;
        int pixelMinX = (int) Math.floor((nextZoneCenterX - nextZoneRadius - viewCenterX) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMaxX = (int) Math.floor((nextZoneCenterX + nextZoneRadius - viewCenterX) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMinZ = (int) Math.floor((nextZoneCenterZ - nextZoneRadius - vierCenterZ) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMaxZ = (int) Math.floor((nextZoneCenterZ + nextZoneRadius - vierCenterZ) / scale + Half_Map_TEXTURE_SIZE);

        byte yellowColor = MapColor.COLOR_YELLOW.getPackedId(MapColor.Brightness.HIGH);

        if (((pz == pixelMaxZ || pz == pixelMinZ) && px >= pixelMinX && px <= pixelMaxX)
                || ((px == pixelMaxX || px == pixelMinX) && pz >= pixelMinZ && pz <= pixelMaxZ)) {
            return yellowColor;
        } else {
            return iniColor;
        }
    }

    public static int drawUnsafeZone(int iniColor, int px, int pz, double viewCenterX, double viewCenterZ) {
        if (zoneLength == 0) {
            return iniColor;
        }
        final int Half_Map_TEXTURE_SIZE = Battleroyalezone.MAP_TEXTURE_SIZE / 2;

        double zoneRadius = zoneLength / 2;
        int pixelMinX = (int) ((zoneCenterX - zoneRadius - viewCenterX) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMaxX = (int) ((zoneCenterX + zoneRadius - viewCenterX) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMinZ = (int) ((zoneCenterZ - zoneRadius - viewCenterZ) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMaxZ = (int) ((zoneCenterZ + zoneRadius - viewCenterZ) / scale + Half_Map_TEXTURE_SIZE);
        if (px <= pixelMinX || px >= pixelMaxX || pz <= pixelMinZ || pz >= pixelMaxZ) {
            return RGBBlender.blendColors(UNSAFE_ZONE_OVERLAY_COLOR, iniColor);
        } else {
            return iniColor;
        }
    }

//    public static int drawExtra(int iniColor, int px, int pz, double viewCenterX, double viewCenterZ, double worldX1, double worldZ1, double worldX2, double worldZ2) {
//
//        final int Half_Map_TEXTURE_SIZE = Battleroyalezone.MAP_TEXTURE_SIZE / 2;
//
//        double minX = Math.min(worldX1, worldX2);
//        double maxX = Math.max(worldX1, worldX2);
//        double minZ = Math.min(worldZ1, worldZ2);
//        double maxZ = Math.max(worldZ1, worldZ2);
//
//
//        int pixelMinX = (int) ((minX - viewCenterX) / scale + Half_Map_TEXTURE_SIZE);
//        int pixelMaxX = (int) ((maxX - viewCenterX) / scale + Half_Map_TEXTURE_SIZE);
//        int pixelMinZ = (int) ((minZ - viewCenterZ) / scale + Half_Map_TEXTURE_SIZE);
//        int pixelMaxZ = (int) ((maxZ - viewCenterZ) / scale + Half_Map_TEXTURE_SIZE);
//
//        if (px <= pixelMinX || px >= pixelMaxX || pz <= pixelMinZ || pz >= pixelMaxZ) {
//            return RGBBlender.blendColors(UNSAFE_ZONE_OVERLAY_COLOR, iniColor);
//        } else {
//            return iniColor;
//        }
//    }


    private static long coordinatesToLong(int x, int z) {
        return (long)x << 32 | z & 0xFFFFFFFFL;
    }

    public static void setZoneCenterX(double zoneCenterX) {
        ClientMapData.zoneCenterX = zoneCenterX;
    }
    public static void setZoneCenterZ(double zoneCenterZ) {
        ClientMapData.zoneCenterZ = zoneCenterZ;
    }
    public static void setNextZoneCenterX(double nextZoneCenterX) {
        ClientMapData.nextZoneCenterX = nextZoneCenterX;
    }
    public static void setNextZoneCenterZ(double nextZoneCenterZ) {
        ClientMapData.nextZoneCenterZ = nextZoneCenterZ;
    }

    public static void setScale(double scale) {
        ClientMapData.scale = Mth.clamp(scale, 0.5, 8);
    }

    public static double getScale() {
        return scale;
    }

    public static void setNextZoneLength(double nextZoneLength) {
        ClientMapData.nextZoneLength = nextZoneLength;
    }

    public static void setZoneLength(double zoneLength) {
        ClientMapData.zoneLength = zoneLength;
    }
}
