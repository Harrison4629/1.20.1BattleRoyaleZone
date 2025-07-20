package net.harrison.battleroyalezone.init;

import net.harrison.battleroyalezone.data.ServerMapData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;


public class MapSample {

    public void sample(CommandSourceStack source , double x1, double z1, double x2, double z2, double height) {
        source.sendSuccess(() -> Component.translatable("sample.battleroyalezone.start").withStyle(ChatFormatting.GREEN), true);

        final boolean useDynamicHeight = height == Double.NEGATIVE_INFINITY;

        new Thread(() -> {
            Level pLevel = source.getLevel();

            final int minChunkX = SectionPos.blockToSectionCoord(Math.min(x1, x2));
            final int maxChunkX = SectionPos.blockToSectionCoord(Math.max(x1, x2));

            final int minChunkZ = SectionPos.blockToSectionCoord(Math.min(z1, z2));
            final int maxChunkZ = SectionPos.blockToSectionCoord(Math.max(z1, z2));

            final long total = (long) (maxChunkX - minChunkX + 1) * (maxChunkZ - minChunkZ + 1);

            long completedChunk = 0;
            long lastProgressReportTime = System.currentTimeMillis();

            int scanY;

            BlockPos.MutableBlockPos mainBlockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos fluidBlockPos = new BlockPos.MutableBlockPos();


            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                double[] lastChunk_previousBlockHeight= new double[16];
                for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                    LevelChunk levelchunk = pLevel.getChunk(cx, cz);
                    for (int x = 0; x < 16; x++) {

                        double previousBlockHeight = lastChunk_previousBlockHeight[x];
                        for (int z = 0; z < 16; z++) {
                            int worldX = levelchunk.getPos().getMinBlockX() + x;
                            int worldZ = levelchunk.getPos().getMinBlockZ() + z;

                            if (!levelchunk.isEmpty()) {
                                double d1 = 0.0D;
                                int l2 = 0;

                                mainBlockPos.set(worldX, 0, worldZ);


                                if (useDynamicHeight) {
                                    scanY = levelchunk.getHeight(Heightmap.Types.WORLD_SURFACE, mainBlockPos.getX(), mainBlockPos.getZ()) + 1;
                                } else {
                                    scanY = (int) height;
                                }

                                BlockState blockstate;
                                if (scanY <= pLevel.getMinBuildHeight() + 1) {
                                    blockstate = Blocks.BEDROCK.defaultBlockState();
                                } else {
                                    do {
                                        --scanY;
                                        mainBlockPos.setY(scanY);
                                        blockstate = levelchunk.getBlockState(mainBlockPos);
                                    } while (blockstate.getMapColor(pLevel, mainBlockPos) == MapColor.NONE && scanY > pLevel.getMinBuildHeight());

                                    if (scanY > pLevel.getMinBuildHeight() && !blockstate.getFluidState().isEmpty()) {
                                        int l3 = scanY - 1;
                                        fluidBlockPos.set(mainBlockPos);

                                        BlockState blockstate1;
                                        do {
                                            fluidBlockPos.setY(l3--);
                                            blockstate1 = levelchunk.getBlockState(fluidBlockPos);
                                            ++l2;
                                        } while (l3 > pLevel.getMinBuildHeight() && !blockstate1.getFluidState().isEmpty());

                                        blockstate = getCorrectStateForFluidBlock(pLevel, blockstate, mainBlockPos);
                                    }
                                }

                                d1 += scanY;

                                MapColor mapcolor = blockstate.getMapColor(pLevel, mainBlockPos);

                                MapColor.Brightness brightness;
                                if (mapcolor == MapColor.WATER) {
                                    double d2 = (double) l2 * 0.1D + (double) (worldX + worldZ & 1) * 0.2D;
                                    if (d2 < 0.5D) {
                                        brightness = MapColor.Brightness.HIGH;
                                    } else if (d2 > 0.9D) {
                                        brightness = MapColor.Brightness.LOW;
                                    } else {
                                        brightness = MapColor.Brightness.NORMAL;
                                    }
                                } else {
                                    double d3 = (d1 - previousBlockHeight) * 4.0D / (double) (1 + 4) + ((double) (worldX + worldZ & 1) - 0.5D) * 0.4D;
                                    if (d3 > 0.6D) {
                                        brightness = MapColor.Brightness.HIGH;
                                    } else if (d3 < -0.6D) {
                                        brightness = MapColor.Brightness.LOW;
                                    } else {
                                        brightness = MapColor.Brightness.NORMAL;
                                    }
                                }

                                previousBlockHeight = d1;
                                if (z == 15) {
                                    lastChunk_previousBlockHeight[x] = previousBlockHeight;
                                }

                                ServerMapData.modifyMapData(worldX, worldZ, mapcolor.getPackedId(brightness));
                            }
                        }
                    }
                    completedChunk++;
                    long now = System.currentTimeMillis();

                    if (now - lastProgressReportTime > 500) {
                        lastProgressReportTime = now;
                        double progress = (double) completedChunk / total;
                        source.getServer().execute(() -> source.sendSuccess(() -> Component.translatable("sample.battleroyalezone.progress").append(String.format("%.2f%%", progress * 100)), true));
                    }
                }
            }

            source.getServer().execute(() -> source.sendSuccess(() -> Component.translatable("command.battleroyalezone.map_sample_success").withStyle(ChatFormatting.YELLOW), true));
        }).start();
    }

    private BlockState getCorrectStateForFluidBlock(Level pLevel, BlockState blockstate, BlockPos.MutableBlockPos mainBlockPos) {
        FluidState fluidstate = blockstate.getFluidState();
        return !fluidstate.isEmpty() && !blockstate.isFaceSturdy(pLevel, mainBlockPos, Direction.UP) ? fluidstate.createLegacyBlock() : blockstate;
    }
}
