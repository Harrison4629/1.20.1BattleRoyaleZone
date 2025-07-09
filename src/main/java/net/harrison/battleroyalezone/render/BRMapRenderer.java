package net.harrison.battleroyalezone.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class BRMapRenderer extends MapRenderer {
    public BRMapRenderer(TextureManager pTextureManager) {
        super(pTextureManager);
    }

    //@Override
    //public void render(@NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pMapId, @NotNull MapItemSavedData pMapData, boolean pActive, int pPackedLight) {
    //    BRMapItemManager manager = BRMapItemManager.viewToManager.get(mapView);
    //    if (manager == null) return;
//
    //    for (int x = 0; x < 128; x++) {
    //        for (int y = 0; y < 128; y++) {
    //            Color color = manager.cachedCanvas[x][y];
    //            if (color != null) {
    //                canvas.setPixelColor(x, y, color);
    //            }
    //        }
    //    }
//
    //}

    private boolean inCanvas(int x, int y) {
        return x >= 0 && x < 128 && y >= 0 && y < 128;
    }
}
