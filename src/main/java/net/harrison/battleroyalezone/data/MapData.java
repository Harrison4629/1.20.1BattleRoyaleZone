package net.harrison.battleroyalezone.data;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.MapColor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapData {
    private static final Map<Long, Byte> iniMapData = new ConcurrentHashMap<>();

//    private static Map<Long, Byte> coloredMapData;
//
//    public static byte[] getColor(Player player, double scale) {
//        byte[] color = new byte[16384];
//
//        for (int px = 0; px < 128; px++) {
//            for (int pz = 0; pz < 128; pz++) {
//                int x = (int) Math.floor(player.getX() + (px - 64) * scale);
//                int z = (int) Math.floor(player.getZ() + (pz - 64) * scale);
//
//                if (coloredMapData.containsKey(coordinatesToLong(x, z))) {
//                    color[px + pz * 128] = coloredMapData.get(coordinatesToLong(x, z));
//                } else {
//                    if ((px + pz & 1) != 0)  {
//                        color[px + pz * 128] = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.NORMAL);
//                    } else {
//                        color[px + pz * 128] = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
//                    }
//                }
//            }
//        }
//        return color;
//    }

    public static void modifyMapData(int x, int z, Byte color) {
        iniMapData.put(coordinatesToLong(x, z), color);
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

    public static void drawBackGround(byte[] color, double playerX, double playerZ , double scale) {
        for (int px = 0; px < 128; px++) {
            for (int pz = 0;pz < 128; pz++) {
                int worldX = (int) (playerX + (px - 64) * scale);
                int worldZ = (int) (playerZ + (pz - 64) * scale);

                if (iniMapData.containsKey(coordinatesToLong(worldX, worldZ))) {
                    color[px + pz * 128] = iniMapData.get(coordinatesToLong(worldX, worldZ));
                } else {
                    if ((px + pz & 1) != 0)  {
                        color[px + pz * 128] = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.NORMAL);
                    } else {
                        color[px + pz * 128] = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
                    }
                }
            }
        }
    }

    public static void drawNextSafeZone(byte[] color, double playerX, double playerZ, double nextZoneCenterX, double nextZoneCenterZ, int nextZoneLength, double scale) {
        int nextZoneRadius = nextZoneLength / 2;
        int pixelMinX = (int) ((nextZoneCenterX - nextZoneRadius - playerX) / scale + 64);
        int pixelMaxX = (int) ((nextZoneCenterX + nextZoneRadius - playerX) / scale + 64);
        int pixelMinZ = (int) ((nextZoneCenterZ - nextZoneRadius - playerZ) / scale + 64);
        int pixelMaxZ = (int) ((nextZoneCenterZ + nextZoneRadius - playerZ) / scale + 64);

        byte yellowColor = MapColor.COLOR_YELLOW.getPackedId(MapColor.Brightness.HIGH);

        if (pixelMinZ >= 0 && pixelMinZ < 128) {//底边
            for (int px = pixelMinX; px <= pixelMaxX; px++) {
                if (px >= 0 && px < 128) {
                    color[px + pixelMinZ * 128] = yellowColor;
                }
            }
        }
        if (pixelMaxZ >= 0 && pixelMaxZ < 128) {//顶边
            for (int px = pixelMinX; px <= pixelMaxX; px++) {
                if (px >= 0 && px < 128) {
                    color[px + pixelMaxZ * 128] = yellowColor;
                }
            }
        }

        if (pixelMinX >= 0 && pixelMinX < 128) {//左边
            for (int pz = pixelMinZ; pz <= pixelMaxZ; pz++) {
                if (pz >=0 && pz < 128) {
                    color[pixelMinX + pz * 128] = yellowColor;
                }
            }
        }
        if (pixelMaxX >= 0 && pixelMaxX < 128) {//右边
            for (int pz = pixelMinZ; pz <= pixelMaxZ; pz++) {
                if (pz >=0 && pz < 128) {
                    color[pixelMaxX + pz * 128] = yellowColor;
                }
            }
        }
    }

    public static void drawUnsafeZone(byte[] color, double playerX, double playerZ, double zoneCenterX, double zoneCenterZ, int zoneLength, double scale) {
        int zoneRadius = zoneLength / 2;
        int pixelMinX = (int) ((zoneCenterX - zoneRadius - playerX) / scale + 64);
        int pixelMaxX = (int) ((zoneCenterX + zoneRadius - playerX) / scale + 64);
        int pixelMinZ = (int) ((zoneCenterZ - zoneRadius - playerZ) / scale + 64);
        int pixelMaxZ = (int) ((zoneCenterZ + zoneRadius - playerZ) / scale + 64);

        for (int pz = 0; pz < Math.min(pixelMinZ, 128); pz++) {//上
            for (int px = 0; px < 128; px++) {
                color[px + pz * 128] = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
            }
        }
        for (int pz = Math.max(pixelMaxZ, 0); pz < 128; pz++) {//下
            for (int px = 0; px < 128; px++) {
                color[px + pz * 128] = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
            }
        }
        for (int pz = Math.max(pixelMinZ, 0); pz <= Math.min(pixelMaxZ, 127); pz++) {
            for (int px = 0; px < Math.min(pixelMinX, 128); px++) {
                color[px + pz * 128] = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
            }
            for (int px = Math.max(pixelMaxX, 0);px < 128; px++) {
                color[px + pz * 128] = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
            }
        }
    }
}
