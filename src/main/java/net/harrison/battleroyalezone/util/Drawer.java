package net.harrison.battleroyalezone.util;

import net.harrison.battleroyalezone.data.ClientMapData;

public class Drawer implements IDrawer{


    private static final double scale = ClientMapData.getScale();
    private static final int MAP_TEXTURE_SIZE = 128; // 假设地图尺寸是 128
    // 假设你有这些常量
    private static final int UNSAFE_ZONE_OVERLAY_COLOR = 0x50FF0000; // 举例：半透明红色

    private final double minX, maxX, minZ, maxZ;

    public Drawer(double worldX1, double worldZ1, double worldX2, double worldZ2) {
        this.minX = Math.min(worldX1, worldX2);
        this.maxX = Math.max(worldX1, worldX2);
        this.minZ = Math.min(worldZ1, worldZ2);
        this.maxZ = Math.max(worldZ1, worldZ2);
    }

    @Override
    public int draw(int currentColor, int px, int pz, double viewCenterX, double viewCenterZ) {
        final int HALF_MAP_TEXTURE_SIZE = MAP_TEXTURE_SIZE / 2;

        // 将世界坐标转换为像素坐标
        int pixelMinX = (int) ((minX - viewCenterX) / scale + HALF_MAP_TEXTURE_SIZE);
        int pixelMaxX = (int) ((maxX - viewCenterX) / scale + HALF_MAP_TEXTURE_SIZE);
        int pixelMinZ = (int) ((minZ - viewCenterZ) / scale + HALF_MAP_TEXTURE_SIZE);
        int pixelMaxZ = (int) ((maxZ - viewCenterZ) / scale + HALF_MAP_TEXTURE_SIZE);

        // 如果当前像素在矩形区域之外，则混合颜色
        if (px <= pixelMinX || px >= pixelMaxX || pz <= pixelMinZ || pz >= pixelMaxZ) {
            // 假设你有一个颜色混合工具类
            return RGBBlender.blendColors(UNSAFE_ZONE_OVERLAY_COLOR, currentColor);
        } else {
            // 在矩形内部，返回原色
            return currentColor;
        }
    }
}
