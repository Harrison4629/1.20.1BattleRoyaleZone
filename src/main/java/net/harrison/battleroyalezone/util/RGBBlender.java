package net.harrison.battleroyalezone.util;

public class RGBBlender {
    public static int blendColors(int color1, int color2) {
        int alpha1 = (color1 >> 24) & 0xFF; // A
        int blue1  = (color1 >> 16) & 0xFF; // B
        int green1 = (color1 >> 8)  & 0xFF; // G
        int red1   = (color1 >> 0)  & 0xFF; // R

        int alpha2 = (color2 >> 24) & 0xFF; // A
        int blue2  = (color2 >> 16) & 0xFF; // B
        int green2 = (color2 >> 8)  & 0xFF; // G
        int red2   = (color2 >> 0)  & 0xFF; // R

        float alphaRatio1 = (float) alpha1 / 255.0F;
        float invAlphaRatio1 = 1.0F - alphaRatio1;

        int finalRed = (int) (red1 * alphaRatio1 + red2 * invAlphaRatio1);
        int finalGreen = (int) (green1 * alphaRatio1 + green2 * invAlphaRatio1);
        int finalBlue = (int) (blue1 * alphaRatio1 + blue2 * invAlphaRatio1);

        int finalAlpha = Math.max(alpha1, alpha2);

        finalRed = Math.min(255, Math.max(0, finalRed));
        finalGreen = Math.min(255, Math.max(0, finalGreen));
        finalBlue = Math.min(255, Math.max(0, finalBlue));
        finalAlpha = Math.min(255, Math.max(0, finalAlpha));

        return (finalAlpha << 24) | (finalBlue << 16) | (finalGreen << 8) | finalRed;
    }
}
