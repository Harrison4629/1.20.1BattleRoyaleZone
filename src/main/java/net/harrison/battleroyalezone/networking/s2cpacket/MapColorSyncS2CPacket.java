package net.harrison.battleroyalezone.networking.s2cpacket;

import net.harrison.battleroyalezone.data.ClientMapData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapColorSyncS2CPacket {
    private final Map<Long, Byte> iniMapData;
    private final boolean isFirstChunk;

    public MapColorSyncS2CPacket(Map<Long, Byte> MapData, boolean isFirstChunk) {
        this.iniMapData = new HashMap<>(MapData);
        this.isFirstChunk = isFirstChunk;
    }

    public MapColorSyncS2CPacket(FriendlyByteBuf buf) {
        this.isFirstChunk = buf.readBoolean();
        this.iniMapData = buf.readMap(FriendlyByteBuf::readLong, FriendlyByteBuf::readByte);
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.isFirstChunk);
        buf.writeMap(this.iniMapData, FriendlyByteBuf::writeLong, (bufByte, val) -> bufByte.writeByte(val));
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientMapData.receiveMapDataChunk(iniMapData, isFirstChunk);
        });
        context.setPacketHandled(true);
    }
}
