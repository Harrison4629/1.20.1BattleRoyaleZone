package net.harrison.battleroyalezone.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Redirect(
            method = {"baseTick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/border/WorldBorder;isWithinBounds(Lnet/minecraft/world/phys/AABB;)Z"
            )
    )
    public boolean mixin_isWithinBounds(WorldBorder wb, AABB bb) {
        return bb.minX > wb.getMinX() && bb.maxX < wb.getMaxX()
                && bb.minZ > wb.getMinZ() && bb.maxZ < wb.getMaxZ();
    }

}
