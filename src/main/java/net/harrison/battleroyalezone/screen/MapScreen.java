package net.harrison.battleroyalezone.screen;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.ClientMapData;
import net.harrison.battleroyalezone.init.ModKeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.MapColor;

public class MapScreen extends Screen {
    private DynamicTexture mapTexture;
    private ResourceLocation mapTextureLocation;
    private static final int MAP_TEXTURE_SIZE = 256;
    private static final ResourceLocation playerIcon = ResourceLocation.fromNamespaceAndPath(Battleroyalezone.MODID, "textures/gui/player_icon.png");

    public MapScreen() {
        super(Component.literal("Tactical Map"));
        this.mapTexture = new DynamicTexture(MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE, true);
        this.mapTextureLocation = Minecraft.getInstance().getTextureManager().register("tactical_map/map", this.mapTexture);
    }

    @Override
    protected void init() {
        super.init();
        updateMapTexture();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);

        updateMapTexture();

        int mapLeft = (this.width - MAP_TEXTURE_SIZE) / 2;
        int mapTop = (this.height - MAP_TEXTURE_SIZE) / 2;
        pGuiGraphics.blit(mapTextureLocation, mapLeft, mapTop, 0, 0, MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE);

        int iconSize = 16;

        pGuiGraphics.blit(playerIcon, mapLeft + (MAP_TEXTURE_SIZE / 2) - (iconSize / 2), mapTop + (MAP_TEXTURE_SIZE / 2) - (iconSize / 2), 0, 0, iconSize, iconSize, iconSize, iconSize);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void updateMapTexture() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        double playerX = player.getX();
        double playerZ = player.getZ();

        for (int x = 0; x < MAP_TEXTURE_SIZE; x++) {
            for (int z = 0; z < MAP_TEXTURE_SIZE; z++) {
                byte colorId = ClientMapData.getBackGroundColor(x, z, playerX, playerZ);

                colorId = ClientMapData.drawNextSafeZone(colorId, x, z, playerX, playerZ);

                int color = MapColor.getColorFromPackedId(colorId);
                color = ClientMapData.drawUnsafeZone(color, x, z, playerX, playerZ);

                if (this.mapTexture.getPixels() != null) {
                    this.mapTexture.getPixels().setPixelRGBA(x, z, color);
                }
            }
        }
        this.mapTexture.upload();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().getTextureManager().release(this.mapTextureLocation);
        super.onClose();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (ModKeyBinds.OPEN_MAP.matches(pKeyCode, pScanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
}
