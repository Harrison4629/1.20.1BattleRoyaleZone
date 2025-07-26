package net.harrison.battleroyalezone.networking.s2cpacket;

import net.harrison.battleroyalezone.data.ClientMapData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MapReferenceSyncS2CPacket {
    private final double zoneCenterX;
    private final double zoneCenterZ;
    private final double nextZoneCenterX;
    private final double nextZoneCenterZ;
    private final double zoneLength;
    private final double nextZoneLength;
    private final boolean reset;

    public MapReferenceSyncS2CPacket(Vec3 zoneCenter, Vec3 nextZoneCenter, double zoneLength, double nextZoneLength, boolean reset) {
        this.zoneCenterX = zoneCenter.x;
        this.zoneCenterZ = zoneCenter.z;
        this.nextZoneCenterX = nextZoneCenter.x;
        this.nextZoneCenterZ = nextZoneCenter.z;
        this.zoneLength = zoneLength;
        this.nextZoneLength = nextZoneLength;
        this.reset = reset;
    }

    public MapReferenceSyncS2CPacket(FriendlyByteBuf buf) {
        this.zoneCenterX = buf.readDouble();
        this.zoneCenterZ = buf.readDouble();
        this.nextZoneCenterX = buf.readDouble();
        this.nextZoneCenterZ = buf.readDouble();
        this.zoneLength = buf.readDouble();
        this.nextZoneLength = buf.readDouble();
        this.reset = buf.readBoolean();
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(zoneCenterX);
        buf.writeDouble(zoneCenterZ);
        buf.writeDouble(nextZoneCenterX);
        buf.writeDouble(nextZoneCenterZ);
        buf.writeDouble(zoneLength);
        buf.writeDouble(nextZoneLength);
        buf.writeBoolean(reset);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {

            if (reset) {
                ClientMapData.reset();
            } else {
                ClientMapData.setZoneCenterX(zoneCenterX);
                ClientMapData.setZoneCenterZ(zoneCenterZ);
                ClientMapData.setNextZoneCenterX(nextZoneCenterX);
                ClientMapData.setNextZoneCenterZ(nextZoneCenterZ);
                ClientMapData.setZoneLength(zoneLength);
                ClientMapData.setNextZoneLength(nextZoneLength);
            }
        });
        context.setPacketHandled(true);
    }

}
