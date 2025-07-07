package net.harrison.battleroyalezone.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
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

    @ModifyArg(
            method = {"baseTick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    ordinal = 1
            ),
            index = 1
    )
    private float modifyBaseDamage(float originalDamage) {

       LivingEntity livingEntity = ((LivingEntity)(Object)this);

       return (float) livingEntity.level().getWorldBorder().getDamagePerBlock();
    }


}
