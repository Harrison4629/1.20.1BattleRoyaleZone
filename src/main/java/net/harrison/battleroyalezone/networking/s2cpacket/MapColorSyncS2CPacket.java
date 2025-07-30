package net.harrison.battleroyalezone.networking.s2cpacket;

import net.harrison.battleroyalezone.data.ClientMapData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapColorSyncS2CPacket {
    private final Map<Long, Byte> iniMapData;
    private final boolean overWrite;
    private final ResourceKey<Level> worldKey;

    public MapColorSyncS2CPacket(Map<Long, Byte> MapData, boolean overWrite, ResourceKey<Level> worldKey) {
        this.iniMapData = new HashMap<>(MapData);
        this.overWrite = overWrite;
        this.worldKey = worldKey;
    }

    public MapColorSyncS2CPacket(FriendlyByteBuf buf) {
        this.overWrite = buf.readBoolean();
        this.iniMapData = buf.readMap(FriendlyByteBuf::readLong, FriendlyByteBuf::readByte);
        this.worldKey = buf.readResourceKey(Registries.DIMENSION);
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.overWrite);
        buf.writeMap(this.iniMapData, FriendlyByteBuf::writeLong, (bufByte, val) -> bufByte.writeByte(val));
        buf.writeResourceKey(this.worldKey);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientMapData.receiveMapDataChunk(worldKey, iniMapData, overWrite);
        });
        context.setPacketHandled(true);
    }
}
