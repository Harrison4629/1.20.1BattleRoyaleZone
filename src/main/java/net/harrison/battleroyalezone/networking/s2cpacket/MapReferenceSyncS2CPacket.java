package net.harrison.battleroyalezone.networking.s2cpacket;

import net.harrison.battleroyalezone.data.ClientMapData;
import net.harrison.battleroyalezone.screen.drawer.NextSafeZoneBorderOverlay;
import net.harrison.battleroyalezone.screen.drawer.UnsafeZoneOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Vector2d;

import java.util.function.Supplier;

public class MapReferenceSyncS2CPacket {

    private final Vector2d previousZoneCenter;
    private final Vector2d nextZoneCenter;
    private final int stateDurationTicks;
    private final double previousZoneLength;
    private final double nextZoneLength;
    private final boolean display;
    private final boolean isShrinking;

    private static final String NextSafeZoneBorderOverlayTag = "NextSafeZoneBorder";
    private static final String UnsafeZoneOverlayTag = "UnsafeZone";


    public MapReferenceSyncS2CPacket(Vec3 previousZoneCenter, Vec3 nextZoneCenter, double previousZoneLength,
                                     double nextZoneLength, int stateDurationTicks, boolean isShrinking, boolean display) {
        this.previousZoneCenter = new Vector2d(previousZoneCenter.x,  previousZoneCenter.z);
        this.nextZoneCenter = new Vector2d(nextZoneCenter.x,  nextZoneCenter.z);
        this.previousZoneLength = previousZoneLength;
        this.nextZoneLength = nextZoneLength;
        this.display = display;
        this.stateDurationTicks =stateDurationTicks;
        this.isShrinking = isShrinking;
    }


    public MapReferenceSyncS2CPacket(FriendlyByteBuf buf) {
        this.previousZoneCenter = new Vector2d(buf.readDouble(), buf.readDouble());
        this.nextZoneCenter = new Vector2d(buf.readDouble(), buf.readDouble());
        this.previousZoneLength = buf.readDouble();
        this.nextZoneLength = buf.readDouble();
        this.stateDurationTicks = buf.readInt();
        this.display = buf.readBoolean();
        this.isShrinking = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(previousZoneCenter.x);
        buf.writeDouble(previousZoneCenter.y);
        buf.writeDouble(nextZoneCenter.x);
        buf.writeDouble(nextZoneCenter.y);
        buf.writeDouble(previousZoneLength);
        buf.writeDouble(nextZoneLength);
        buf.writeInt(stateDurationTicks);
        buf.writeBoolean(display);
        buf.writeBoolean(isShrinking);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientMapData.clearOverlays();
            if (display) {
                ClientMapData.addOverlay(new NextSafeZoneBorderOverlay(nextZoneCenter.x, nextZoneCenter.y, nextZoneLength / 2), NextSafeZoneBorderOverlayTag);
                ClientMapData.addOverlay(new UnsafeZoneOverlay(), UnsafeZoneOverlayTag);
            } else {
                ClientMapData.clearOverlays();
            }
            ClientMapData.setManager(previousZoneLength, nextZoneLength, previousZoneCenter,
                    nextZoneCenter, stateDurationTicks, isShrinking);
        });
        context.setPacketHandled(true);
    }
}
