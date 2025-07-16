package net.harrison.battleroyalezone.data;

import net.harrison.battleroyalezone.util.RGBBlender;
import net.minecraft.world.level.material.MapColor;

import java.util.HashMap;
import java.util.Map;

public class ClientMapData {
    private static final int MAP_TEXTURE_SIZE = 256;

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
        scale = 1.0F;
    }

    public static void receiveMapDataChunk(Map<Long, Byte> chunkData, boolean isFirstChunk) {
        if (isFirstChunk) {
            iniMapData.clear();
        }
        iniMapData.putAll(chunkData);
    }


    public static byte getBackGroundColor(int px, int pz, double playerX, double playerZ) {

        int pRadius = MAP_TEXTURE_SIZE / 2;

        int worldX = (int) Math.floor(playerX + (px + 0.5 - pRadius) * scale);
        int worldZ = (int) Math.floor(playerZ + (pz + 0.5 - pRadius) * scale);

        if (iniMapData.containsKey(coordinatesToLong(worldX, worldZ))) {
            return iniMapData.get(coordinatesToLong(worldX, worldZ));
        } else if ((px + pz & 1) == 0) {
            return MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.NORMAL);
        } else {
            return MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
        }
    }

    public static byte drawNextSafeZone(byte iniColor, int px, int pz, double playerX, double playerZ) {
        final int Half_Map_TEXTURE_SIZE = MAP_TEXTURE_SIZE / 2;

        double nextZoneRadius = nextZoneLength / 2;
        int pixelMinX = (int) Math.floor((nextZoneCenterX - nextZoneRadius - playerX) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMaxX = (int) Math.floor((nextZoneCenterX + nextZoneRadius - playerX) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMinZ = (int) Math.floor((nextZoneCenterZ - nextZoneRadius - playerZ) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMaxZ = (int) Math.floor((nextZoneCenterZ + nextZoneRadius - playerZ) / scale + Half_Map_TEXTURE_SIZE);

        byte yellowColor = MapColor.COLOR_YELLOW.getPackedId(MapColor.Brightness.HIGH);

        if (((pz == pixelMaxZ || pz == pixelMinZ) && px >= pixelMinX && px <= pixelMaxX)
                || ((px == pixelMaxX || px == pixelMinX) && pz >= pixelMinZ && pz <= pixelMaxZ)) {
            return yellowColor;
        } else {
            return iniColor;
        }
    }

    public static int drawUnsafeZone(int iniColor, int px, int pz, double playerX, double playerZ) {
        if (zoneLength == 0) {
            return iniColor;
        }
        final int Half_Map_TEXTURE_SIZE = MAP_TEXTURE_SIZE / 2;

        double zoneRadius = zoneLength / 2;
        int pixelMinX = (int) ((zoneCenterX - zoneRadius - playerX) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMaxX = (int) ((zoneCenterX + zoneRadius - playerX) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMinZ = (int) ((zoneCenterZ - zoneRadius - playerZ) / scale + Half_Map_TEXTURE_SIZE);
        int pixelMaxZ = (int) ((zoneCenterZ + zoneRadius - playerZ) / scale + Half_Map_TEXTURE_SIZE);
        if (px <= pixelMinX || px >= pixelMaxX || pz <= pixelMinZ || pz >= pixelMaxZ) {
            return RGBBlender.blendColors(0x80E6D8AD, iniColor);
        } else {
            return iniColor;
        }
    }


    private static long coordinatesToLong(int x, int z) {
        return (long)x << 32 | z & 0xFFFFFFFFL;
    }

    private static int longToX(long key) {
        return (int) (key >> 32);
    }

    private static int longToZ(long key) {
        return (int) key;
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
        ClientMapData.scale = Math.max(scale, 0.5);
    }

    public static void setNextZoneLength(double nextZoneLength) {
        ClientMapData.nextZoneLength = nextZoneLength;
    }

    public static void setZoneLength(double zoneLength) {
        ClientMapData.zoneLength = zoneLength;
    }
}
