package net.harrison.battleroyalezone.mixin;

import net.harrison.battleroyalezone.config.MapConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItem.class)
public class MapItemMixin {

    @Inject(
            method = {"update"},
            at = @At(value = "TAIL")
    )
    private void ZoneRender(Level pLevel, Entity pViewer, MapItemSavedData pData, CallbackInfo ci) {

        int rectMidX = MapConfig.getMidX(pData);
        int rectMidZ = MapConfig.getMidZ(pData);

        int squareSideLength = MapConfig.getSquareSideLength(pData);

        int borderWidth = 1;
        byte borderColor = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.NORMAL);

        for (int px = rectMidX - squareSideLength / 2; px < rectMidX + squareSideLength / 2; px++) {
            for (int i = 0; i < borderWidth; i++) {

                // 顶部边框
                if (px >= 0 && px < MapItem.IMAGE_WIDTH && (rectMidZ - squareSideLength / 2 + i) >= 0 && (rectMidZ - squareSideLength / 2 + i) < MapItem.IMAGE_HEIGHT) {
                    pData.colors[px + (rectMidZ - squareSideLength / 2 + i) * MapItem.IMAGE_WIDTH] = borderColor;
                }
                // 底部边框
                if (px >= 0 && px < MapItem.IMAGE_WIDTH && (rectMidZ + squareSideLength / 2 - i) >= 0 && (rectMidZ + squareSideLength / 2 - i) < MapItem.IMAGE_HEIGHT) {
                    pData.colors[px + (rectMidZ + squareSideLength / 2 - i) * MapItem.IMAGE_WIDTH] = borderColor;
                }
            }
        }

        for (int pz = rectMidZ - squareSideLength  / 2; pz < rectMidZ + squareSideLength / 2; pz++) {
            for (int i = 0; i < borderWidth; i++) {
                // 左侧边框
                if (pz >= 0 && pz < MapItem.IMAGE_HEIGHT && (rectMidX - squareSideLength / 2 + i) >= 0 && (rectMidX - squareSideLength / 2 + i) < MapItem.IMAGE_WIDTH) {
                    pData.colors[(rectMidX - squareSideLength / 2 + i) + pz * MapItem.IMAGE_WIDTH] = borderColor;
                }
                // 右侧边框
                if (pz >= 0 && pz < MapItem.IMAGE_HEIGHT && (rectMidX + squareSideLength / 2 - i) >= 0 && (rectMidX + squareSideLength / 2 - i) < MapItem.IMAGE_WIDTH) {
                    pData.colors[(rectMidX + squareSideLength / 2 - i) + pz * MapItem.IMAGE_WIDTH] = borderColor;
                }
            }
        }
    }
}
