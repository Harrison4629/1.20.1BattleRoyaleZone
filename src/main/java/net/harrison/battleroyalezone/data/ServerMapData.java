package net.harrison.battleroyalezone.data;

import com.google.common.collect.Maps;
import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.init.ModMessages;
import net.harrison.battleroyalezone.networking.s2cpacket.MapColorSyncS2CPacket;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMapData extends SavedData {

    private final Map<Long, Byte> mapData = new ConcurrentHashMap<>();
    private static final int PACKET_SIZE = 10000;

    public ServerMapData(CompoundTag nbt) {
        if (nbt.contains("keys", 12) && nbt.contains("values", 7)) { // 12=LongArray, 7=ByteArray
            long[] keys = nbt.getLongArray("keys");
            byte[] values = nbt.getByteArray("values");

            if (keys.length == values.length) {
                for (int i = 0; i < keys.length; i++) {
                    mapData.put(keys[i], values[i]);
                }
            }
        }
    }

    public ServerMapData() {
    }

    public static ServerMapData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(
                ServerMapData::new,
                ServerMapData::new,
                Battleroyalezone.MODID + "_mapdata"
        );
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        List<Long> keys = new ArrayList<>(mapData.size());
        List<Byte> values = new ArrayList<>(mapData.size());

        mapData.forEach((key, value) -> {
            keys.add(key);
            values.add(value);
        });

        byte[] valuesArray = new byte[values.size()];
        for(int i = 0; i < values.size(); i++) {
            valuesArray[i] = values.get(i);
        }

        pCompoundTag.put("keys", new LongArrayTag(keys));
        pCompoundTag.put("values", new ByteArrayTag(valuesArray));
        return pCompoundTag;
    }

    public void modifyMapData(int x, int z, Byte color) {
        mapData.put(coordinatesToLong(x, z), color);
        setDirty();
    }

    public void pushMapData(ServerPlayer player) {
        Iterator<Map.Entry<Long, Byte>> iterator = mapData.entrySet().iterator();
        Map<Long, Byte> onePacket = Maps.newHashMap();
        boolean isFirstChunk = true;

        while (iterator.hasNext()) {
            Map.Entry<Long, Byte> entry = iterator.next();
            onePacket.put(entry.getKey(), entry.getValue());

            if (onePacket.size() >= PACKET_SIZE) {
                ModMessages.sendToPlayer(new MapColorSyncS2CPacket(onePacket, isFirstChunk), player);
                onePacket.clear();
                isFirstChunk = false;
            }
        }

        if (!onePacket.isEmpty() || isFirstChunk) {
            ModMessages.sendToPlayer(new MapColorSyncS2CPacket(onePacket, isFirstChunk), player);
        }
    }

    private long coordinatesToLong(int x, int z) {
        return (long)x << 32 | z & 0xFFFFFFFFL;
    }
}
