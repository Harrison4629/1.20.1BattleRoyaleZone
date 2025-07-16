package net.harrison.battleroyalezone.data;

import com.google.common.collect.Maps;
import net.harrison.battleroyalezone.init.ModMessages;
import net.harrison.battleroyalezone.networking.s2cpacket.MapColorSyncS2CPacket;
import net.minecraft.world.level.material.MapColor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMapData {
    private static final Map<Long, Byte> iniMapData = new ConcurrentHashMap<>();

    private static final int PacketSize = 10000;

    public static void modifyMapData(int x, int z, Byte color) {
        iniMapData.put(coordinatesToLong(x, z), color);
    }

    private static long coordinatesToLong(int x, int z) {
        return (long)x << 32 | z & 0xFFFFFFFFL;
    }

    public static void pushMapData() {
        Iterator<Map.Entry<Long, Byte>> iterator = iniMapData.entrySet().iterator();
        Map<Long, Byte> onePacket = Maps.newHashMap();

        boolean isFirstChunk = true;

        while (iterator.hasNext()) {

            Map.Entry<Long, Byte> entry = iterator.next();
            onePacket.put(entry.getKey(), entry.getValue());

            if (onePacket.size() >= PacketSize) {
                ModMessages.sendToAllPlayer(new MapColorSyncS2CPacket(onePacket, isFirstChunk));
                onePacket.clear();
                isFirstChunk = false;
            }
        }

        if (!onePacket.isEmpty()) {
            ModMessages.sendToAllPlayer(new MapColorSyncS2CPacket(onePacket, isFirstChunk));
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
