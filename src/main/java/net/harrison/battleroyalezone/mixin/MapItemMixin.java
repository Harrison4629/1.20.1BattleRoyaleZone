package net.harrison.battleroyalezone.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItem.class)
public class MapItemMixin {

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void update(Level pLevel, Entity pViewer, MapItemSavedData pData, CallbackInfo ci) {
        ci.cancel();
    }





    
//    @Unique
//    private static double Scaling_Factor;
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public void sample(Level pLevel, Entity pViewer, MapItemSavedData pData) {
//        if (pLevel.dimension() == pData.dimension && pViewer instanceof Player) {
//            if (MapConfig.event == null || !MapConfig.event.getRunningState()) {
//                Scaling_Factor = Math.min(ZoneConfig.getZoneSize(ZoneConfig.getMaxStage() - 1) / 128F, 5);
//            } else {
//                if (MapConfig.event.getState() == ZoneStateEnum.SHRINKING) {
//                    Scaling_Factor = Math.min(MapConfig.event.getCurrentZoneSize() / 128F, 5);
//                }
//            }
//            Scaling_Factor = Scaling_Factor < 0.5 ? 0.5 : Scaling_Factor;
//
//            int j = pData.centerX;
//            int k = pData.centerZ;
//
//            MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer = pData.getHoldingPlayer((Player)pViewer);
//            ++mapitemsaveddata$holdingplayer.step;
//            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
//            BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
//
//            for(int k1 = 0; k1 < 128; ++k1) {
//                double d0 = 0.0D;
//
//                for(int l1 = 0; l1 < 128; ++l1) {
//                    double j2 = (j / Scaling_Factor + k1 - 64) * Scaling_Factor;
//                    double k2 = (k / Scaling_Factor + l1 - 64) * Scaling_Factor;
//                    Multiset<MapColor> multiset = LinkedHashMultiset.create();
//                    LevelChunk levelchunk = pLevel.getChunk(SectionPos.blockToSectionCoord(j2), SectionPos.blockToSectionCoord(k2));
//                    if (!levelchunk.isEmpty()) {
//                        double l2 = 0;
//                        double d1 = 0.0D;
//
//                        for (int i4 = 0; i4 < Scaling_Factor; ++i4) {
//                            for (int j3 = 0; j3 < Scaling_Factor; ++j3) {
//                                blockpos$mutableblockpos.set(j2 + i4, 0, k2 + j3);
//                                int k3 = levelchunk.getHeight(Heightmap.Types.WORLD_SURFACE, blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getZ()) + 1;
//                                BlockState blockstate;
//                                if (k3 <= pLevel.getMinBuildHeight() + 1) {
//                                    blockstate = Blocks.BEDROCK.defaultBlockState();
//                                } else {
//                                    do {
//                                        --k3;
//                                        blockpos$mutableblockpos.setY(k3);
//                                        blockstate = levelchunk.getBlockState(blockpos$mutableblockpos);
//                                    } while (blockstate.getMapColor(pLevel, blockpos$mutableblockpos) == MapColor.NONE && k3 > pLevel.getMinBuildHeight());
//
//                                    if (k3 > pLevel.getMinBuildHeight() && !blockstate.getFluidState().isEmpty()) {
//                                        int l3 = k3 - 1;
//                                        blockpos$mutableblockpos1.set(blockpos$mutableblockpos);
//
//                                        BlockState blockstate1;
//                                        do {
//                                            blockpos$mutableblockpos1.setY(l3--);
//                                            blockstate1 = levelchunk.getBlockState(blockpos$mutableblockpos1);
//                                            ++l2;
//                                        } while (l3 > pLevel.getMinBuildHeight() && !blockstate1.getFluidState().isEmpty());
//
//                                        blockstate = this.getCorrectStateForFluidBlock(pLevel, blockstate, blockpos$mutableblockpos);
//                                    }
//                                }
//
//                                pData.checkBanners(pLevel, blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getZ());
//                                d1 += (double) k3 / (Scaling_Factor * Scaling_Factor);
//                                multiset.add(blockstate.getMapColor(pLevel, blockpos$mutableblockpos));
//                            }
//                        }
//
//                        l2 /= Scaling_Factor * Scaling_Factor;
//                        MapColor mapcolor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MapColor.NONE);
//                        MapColor.Brightness mapcolor$brightness;
//                        if (mapcolor == MapColor.WATER) {
//                            double d2 = l2 * 0.1D + (double)(k1 + l1 & 1) * 0.2D;
//                            if (d2 < 0.5D) {
//                                mapcolor$brightness = MapColor.Brightness.HIGH;
//                            } else if (d2 > 0.9D) {
//                                mapcolor$brightness = MapColor.Brightness.LOW;
//                            } else {
//                                mapcolor$brightness = MapColor.Brightness.NORMAL;
//                            }
//                        } else {
//                            double d3 = (d1 - d0) * 4.0D / (Scaling_Factor + 4) + ((double)(k1 + l1 & 1) - 0.5D) * 0.4D;
//                            if (d3 > 0.6D) {
//                                mapcolor$brightness = MapColor.Brightness.HIGH;
//                            } else if (d3 < -0.6D) {
//                                mapcolor$brightness = MapColor.Brightness.LOW;
//                            } else {
//                                mapcolor$brightness = MapColor.Brightness.NORMAL;
//                            }
//                        }
//                        d0 = d1;
//                        pData.updateColor(k1, l1, mapcolor.getPackedId(mapcolor$brightness));
//                    }
//                }
//            }
//
//        }
//
//        int rectMidX = MapConfig.getMidX(pData);
//        int rectMidZ = MapConfig.getMidZ(pData);
//
//        int squareSideLength = MapConfig.getSquareSideLength(pData);
//
//        byte borderColor = MapColor.COLOR_BLUE.getPackedId(MapColor.Brightness.HIGH);
//
//        for (int pz = 0; pz < MapItem.IMAGE_HEIGHT; pz++) {
//            for (int px = 0; px < MapItem.IMAGE_WIDTH; px++) {
//                if ((pz < rectMidZ - squareSideLength / 2 || pz > rectMidZ + squareSideLength / 2) || (px < rectMidX - squareSideLength / 2 || px > rectMidX + squareSideLength / 2)) {
//                    pData.colors[px + pz * MapItem.IMAGE_WIDTH] = blend_Color(borderColor, pData.colors[px + pz * MapItem.IMAGE_WIDTH]);
//                }
//            }
//        }
//
//        //for (int px = rectMidX - squareSideLength / 2; px < rectMidX + squareSideLength / 2; px++) {
//        //    // 顶部边框
//        //    if (px >= 0 && px < MapItem.IMAGE_WIDTH && (rectMidZ - squareSideLength / 2) >= 0 && (rectMidZ - squareSideLength / 2) < MapItem.IMAGE_HEIGHT) {
//        //        pData.colors[px + (rectMidZ - squareSideLength / 2) * MapItem.IMAGE_WIDTH] = borderColor;
//        //    }
//        //    // 底部边框
//        //    if (px >= 0 && px < MapItem.IMAGE_WIDTH && (rectMidZ + squareSideLength / 2) >= 0 && (rectMidZ + squareSideLength / 2) < MapItem.IMAGE_HEIGHT) {
//        //        pData.colors[px + (rectMidZ + squareSideLength / 2) * MapItem.IMAGE_WIDTH] = borderColor;
//        //    }
//        //}
//        //
//        //for (int pz = rectMidZ - squareSideLength  / 2; pz < rectMidZ + squareSideLength / 2; pz++) {
//        //    // 左侧边框
//        //    if (pz >= 0 && pz < MapItem.IMAGE_HEIGHT && (rectMidX - squareSideLength / 2) >= 0 && (rectMidX - squareSideLength / 2) < MapItem.IMAGE_WIDTH) {
//        //        pData.colors[(rectMidX - squareSideLength / 2) + pz * MapItem.IMAGE_WIDTH] = borderColor;
//        //    }
//        //    // 右侧边框
//        //    if (pz >= 0 && pz < MapItem.IMAGE_HEIGHT && (rectMidX + squareSideLength / 2) >= 0 && (rectMidX + squareSideLength / 2) < MapItem.IMAGE_WIDTH) {
//        //        pData.colors[(rectMidX + squareSideLength / 2) + pz * MapItem.IMAGE_WIDTH] = borderColor;
//        //    }
//        //}
//    }
//
//    @Unique
//    private static byte blend_Color(byte a, byte b) {
//        int RGBa = get_Rgb(a);
//        int RGBb = get_Rgb(b);
//
//        int mixedRGB = mix_Rgb(RGBa, RGBb);
//
//        return findClosestPackedId(mixedRGB);
//    }
//
//    @Unique
//    private static int get_Rgb(byte packedId) {
//        // 将 byte 转换为无符号整数
//        int packedInt = packedId & 0xFF;
//
//        // 提取 Material ID (高6位)
//        int materialId = packedInt >> 2;
//        // 提取 Brightness ID (低2位)
//        int brightnessId = packedInt & 3; // 相当于 & 0b11
//
//        // 获取对应的 MapColor 和 Brightness 对象
//        MapColor mapColor = MapColor.byId(materialId);
//        MapColor.Brightness brightness = MapColor.Brightness.byId(brightnessId);
//
//        // 计算并返回最终的 RGB 颜色
//        // 注意：calculateRGBColor 返回的是 ARGB，格式为 0xAARRGGBB
//        return mapColor.calculateRGBColor(brightness);
//    }
//
//    @Unique
//    private static int mix_Rgb(int color1, int color2) {
//        // 提取 color1 的 R, G, B 分量
//        int r1 = (color1 >> 16) & 0xFF;
//        int g1 = (color1 >> 8) & 0xFF;
//        int b1 = color1 & 0xFF;
//
//        // 提取 color2 的 R, G, B 分量
//        int r2 = (color2 >> 16) & 0xFF;
//        int g2 = (color2 >> 8) & 0xFF;
//        int b2 = color2 & 0xFF;
//
//        // 计算平均值
//        int mixedR = (r1 + r2) / 2;
//        int mixedG = (g1 + g2) / 2;
//        int mixedB = (b1 + b2) / 2;
//
//        // 重新组合为 ARGB 颜色 (Alpha 为 255，即不透明)
//        return 0xFF000000 | (mixedR << 16) | (mixedG << 8) | mixedB;
//    }
//
//    private static byte findClosestPackedId(int targetRgb) {
//        double minDistanceSq = Double.MAX_VALUE;
//        byte bestPackedId = 0;
//
//        int tr = (targetRgb >> 16) & 0xFF;
//        int tg = (targetRgb >> 8) & 0xFF;
//        int tb = targetRgb & 0xFF;
//
//        // 遍历所有可能的 MapColor 和 Brightness 组合
//        for (int i = 0; i < 64; i++) {
//            MapColor currentColor = MapColor.byId(i);
//            if (currentColor == MapColor.NONE && i > 0) continue;
//
//            for (MapColor.Brightness currentBrightness : MapColor.Brightness.values()) {
//                int candidateRgb = currentColor.calculateRGBColor(currentBrightness);
//
//                int cr = (candidateRgb >> 16) & 0xFF;
//                int cg = (candidateRgb >> 8) & 0xFF;
//                int cb = candidateRgb & 0xFF;
//
//                // 计算颜色距离的平方（比开方更快）
//                double distanceSq = Math.pow(tr - cr, 2) + Math.pow(tg - cg, 2) + Math.pow(tb - cb, 2);
//
//                if (distanceSq < minDistanceSq) {
//                    minDistanceSq = distanceSq;
//                    bestPackedId = currentColor.getPackedId(currentBrightness);
//                }
//            }
//        }
//        return bestPackedId;
//    }
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    private BlockState getCorrectStateForFluidBlock(Level pLevel, BlockState pState, BlockPos pPos) {
//        FluidState fluidstate = pState.getFluidState();
//        return !fluidstate.isEmpty() && !pState.isFaceSturdy(pLevel, pPos, Direction.UP) ? fluidstate.createLegacyBlock() : pState;
//    }
}
