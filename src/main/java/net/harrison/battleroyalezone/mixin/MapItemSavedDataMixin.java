package net.harrison.battleroyalezone.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MapItemSavedData.class)
public interface MapItemSavedDataMixin {
    @Invoker("<init>")
    static MapItemSavedData create_Map_Data(int pX, int pZ, byte pScale, boolean pTrackingPosition, boolean pUnlimitedTracking, boolean pLocked, ResourceKey<Level> pDimension) {
        throw new AssertionError();
    }
}
