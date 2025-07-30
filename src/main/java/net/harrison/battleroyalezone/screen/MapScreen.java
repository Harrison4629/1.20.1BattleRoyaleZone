package net.harrison.battleroyalezone.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
    public static final int MAP_TEXTURE_SIZE = 256;
    public static final int Half_Map_TEXTURE_SIZE = MAP_TEXTURE_SIZE / 2;

    private final DynamicTexture mapTexture;
    private final ResourceLocation mapTextureLocation;
    private static final ResourceLocation Pointer = ResourceLocation.fromNamespaceAndPath(Battleroyalezone.MODID, "textures/gui/pointer.png");
    private static final ResourceLocation Border = ResourceLocation.fromNamespaceAndPath(Battleroyalezone.MODID, "textures/gui/border.png");

    private double mapOffsetX = 0.0;
    private double mapOffsetZ = 0.0;

    public MapScreen() {
        super(Component.literal("Tactical Map"));
        this.mapTexture = new DynamicTexture(MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE, true);
        this.mapTextureLocation = Minecraft.getInstance().getTextureManager().register("tactical_map/map", this.mapTexture);
    }

    @Override
    protected void init() {
        super.init();
        this.mapOffsetX = 0;
        this.mapOffsetZ = 0;
        updateMapTexture();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);

        updateMapTexture();

        int mapLeft = (this.width - MAP_TEXTURE_SIZE) / 2;
        int mapTop = (this.height - MAP_TEXTURE_SIZE) / 2;
        pGuiGraphics.blit(mapTextureLocation, mapLeft, mapTop, 0, 0, MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE);

        pGuiGraphics.blit(Border, mapLeft, mapTop, 0, 0, MAP_TEXTURE_SIZE, MAP_TEXTURE_SIZE);

        renderPlayerPointer(pGuiGraphics, mapLeft, mapTop);

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderPlayerPointer(GuiGraphics pGuiGraphics, int mapLeft, int mapTop) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        int iconSize = (int) (16 / ClientMapData.getScale());
        float screenCenterX = mapLeft + (MAP_TEXTURE_SIZE / 2.0f);
        float screenCenterY = mapTop + (MAP_TEXTURE_SIZE / 2.0f);

        float pointerX = screenCenterX - (float)(this.mapOffsetX / ClientMapData.getScale());
        float pointerY = screenCenterY - (float)(this.mapOffsetZ / ClientMapData.getScale());

        float playerYaw = player.getYRot();
        float rotationAngle = playerYaw + 180.0f;

        PoseStack poseStack = pGuiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(pointerX, pointerY, 0);

        poseStack.mulPose(Axis.ZP.rotationDegrees(rotationAngle));

        pGuiGraphics.blit(Pointer, -iconSize / 2, -iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize);

        poseStack.popPose();
    }

    private void updateMapTexture() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        double mapWorldX = player.getX() + this.mapOffsetX;
        double mapWorldZ = player.getZ() + this.mapOffsetZ;

        for (int x = 0; x < MAP_TEXTURE_SIZE; x++) {
            for (int z = 0; z < MAP_TEXTURE_SIZE; z++) {

                double worldX =  Math.floor(mapWorldX + (x + 0.5 - Half_Map_TEXTURE_SIZE) * ClientMapData.getScale());
                double worldZ =  Math.floor(mapWorldZ + (z + 0.5 - Half_Map_TEXTURE_SIZE) * ClientMapData.getScale());

                byte colorId = ClientMapData.getBackGroundColor(worldX, worldZ);

                int color = MapColor.getColorFromPackedId(colorId);

                if (!ClientMapData.OverLaysIsEmpty()) {
                    color = ClientMapData.applyOverlays(color, x, z, mapWorldX, mapWorldZ, ClientMapData.getScale());
                }


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

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        double zoomFactor = 1.1;
        double newScale;

        if (pDelta > 0) {
            newScale = ClientMapData.getScale() / zoomFactor;
        } else {
            newScale = ClientMapData.getScale() * zoomFactor;
        }

        ClientMapData.setScale(newScale);

        return true;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            int mapLeft = (this.width - MAP_TEXTURE_SIZE) / 2;
            int mapTop = (this.height - MAP_TEXTURE_SIZE) / 2;
            if (pMouseX >= mapLeft && pMouseX < mapLeft + MAP_TEXTURE_SIZE &&
                    pMouseY >= mapTop && pMouseY < mapTop + MAP_TEXTURE_SIZE) {
                this.setDragging(true);
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.setDragging(false);
            return true;
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.isDragging() && pButton == 0) {
            this.mapOffsetX -= pDragX * ClientMapData.getScale();
            this.mapOffsetZ -= pDragY * ClientMapData.getScale();
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
}
