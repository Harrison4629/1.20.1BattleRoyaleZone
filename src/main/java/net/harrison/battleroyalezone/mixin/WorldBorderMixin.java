package net.harrison.battleroyalezone.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldBorder.class)
public class WorldBorderMixin {
    @Inject(
            method = {"isWithinBounds(DD)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void mixin_isWithinBounds_0(double x, double z, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(
            method = {"isWithinBounds(DDD)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void mixin_isWithinBounds_1(double x, double z, double offset, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(
            method = {"isWithinBounds(Lnet/minecraft/world/phys/AABB;)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void mixin_isWithinBounds_2(AABB aabb, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(
            method = {"isWithinBounds(Lnet/minecraft/core/BlockPos;)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void mixin_isWithinBounds_3(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(
            method = {"isWithinBounds(Lnet/minecraft/world/level/ChunkPos;)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void mixin_isWithinBounds_4(ChunkPos chunkPos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(
            method = {"isInsideCloseToBorder"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void mixin_isInsideCloseToBorder(Entity entity, AABB aabb, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
