package net.harrison.battleroyalezone.data;

import com.google.common.collect.Maps;
import net.harrison.battleroyalezone.init.ModMessages;
import net.harrison.battleroyalezone.networking.s2cpacket.MapColorSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;

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

    public static void pushMapData(ServerPlayer player) {
        Iterator<Map.Entry<Long, Byte>> iterator = iniMapData.entrySet().iterator();
        Map<Long, Byte> onePacket = Maps.newHashMap();

        boolean isFirstChunk = true;

        while (iterator.hasNext()) {

            Map.Entry<Long, Byte> entry = iterator.next();
            onePacket.put(entry.getKey(), entry.getValue());

            if (onePacket.size() >= PacketSize) {
                ModMessages.sendToPlayer(new MapColorSyncS2CPacket(onePacket, isFirstChunk), player);
                onePacket.clear();
                isFirstChunk = false;
            }
        }

        if (!onePacket.isEmpty()) {
            ModMessages.sendToPlayer(new MapColorSyncS2CPacket(onePacket, isFirstChunk), player);
        }

    }
}
