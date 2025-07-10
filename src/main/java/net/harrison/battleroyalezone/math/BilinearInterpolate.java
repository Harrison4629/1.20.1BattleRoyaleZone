package net.harrison.battleroyalezone.math;

import net.minecraft.world.item.MapItem;

public class BilinearInterpolate {
    private final int MaxWidth = MapItem.IMAGE_WIDTH;
    private final int MaxHeight = MapItem.IMAGE_HEIGHT;

    public byte[] ScaleDown(Byte[][] iniArray , float scale) {


        byte[] result = new byte[MaxWidth * MaxHeight];

        for (int z = 0; z < MaxHeight; z++) {
            for (int x = 0; x < MaxWidth; x++) {
                float xOriginal = (x + 0.5f) / scale - 0.5f;
                float zOriginal = (z + 0.5f) / scale - 0.5f;

                // 获取原始图像中周围的四个整数坐标像素
                int x1 = (int) Math.floor(xOriginal);
                int z1 = (int) Math.floor(zOriginal);
                // 确保索引不会超出原始图像边界
                int x2 = (int) Math.min(Math.ceil(xOriginal), iniArray.length - 1);
                int z2 = (int) Math.min(Math.ceil(zOriginal), iniArray[0].length - 1);

                // 计算插值权重（到像素块左上角的距离分数）
                float dx = xOriginal - x1;
                float dy = zOriginal - z1;

                // 检索颜色值（作为整数以避免字节算术问题）
                // 需要将 byte 转换为 int 以确保正确的算术 (0-255 范围)
                int val11 = (iniArray[z1][x1] & 0xFF); // 将 byte 转换为无符号 int (0-255)
                int val12 = (iniArray[z1][x2] & 0xFF);
                int val21 = (iniArray[z2][x1] & 0xFF);
                int val22 = (iniArray[z2][x2] & 0xFF);

                // 执行双线性插值
                // 首先，水平插值
                float interp1 = val11 * (1.0f - dx) + val12 * dx;
                float interp2 = val21 * (1.0f - dx) + val22 * dx;

                // 然后，垂直插值
                float finalValue = interp1 * (1.0f - dy) + interp2 * dy;

                // 将值钳制到 0-255 范围并转换回 byte
                byte pixelValue = (byte) Math.round(Math.max(0, Math.min(255, finalValue)));

                // 将结果存储到一维数组中
                result[x + z * MaxWidth] = pixelValue;
            }
        }

        return result;

    }
}
