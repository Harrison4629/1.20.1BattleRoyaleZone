package net.harrison.battleroyalezone.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.harrison.basicdevtool.init.ModMessages;
import net.harrison.basicdevtool.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.harrison.basicdevtool.util.DelayTask;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.ZoneStagePublisherEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("brzone")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("start")
                        .executes(context -> {
                            ZoneStagePublisherEvent.startZoneSystem(context.getSource());
                            return 1;
                        })
                )

                .then(Commands.literal("info")
                        .executes(context -> {
                            MinecraftServer server = context.getSource().getServer();

                            String fullInfo = ZoneConfig.getInfo();
                            String[] lines = fullInfo.split("\n");

                            for (int i = 0; i < lines.length; i++) {
                                final String currentLine = lines[i];
                                final int delayTicks = i * 5;

                                DelayTask.schedule(() -> server.getPlayerList().broadcastSystemMessage(Component.literal(currentLine), false), delayTicks);
                            }

                            ModMessages.sendToAllPlayer(new PlaySoundToClientS2CPacket(SoundEvents.EXPERIENCE_ORB_PICKUP,
                                    1.0F, 1.0F));
                            return 1;
                        })
                )

                .then(Commands.literal("stop")
                        .executes(context -> {
                            ZoneStagePublisherEvent.stopZoneSystem();
                            return 1;
                        })
                )
        );


        dispatcher.register(Commands.literal("samplemap")
                .requires(sourceStack -> sourceStack.hasPermission(2))
                .then(Commands.argument("pos1", Vec2Argument.vec2())
                        .then(Commands.argument("pos2", Vec2Argument.vec2())
                                .executes(context -> {
                                    final double x1 = Vec2Argument.getVec2(context, "pos1").x;
                                    final double z1 = Vec2Argument.getVec2(context, "pos1").y;
                                    final double x2 = Vec2Argument.getVec2(context, "pos2").x;
                                    final double z2 = Vec2Argument.getVec2(context, "pos2").y;

                                    MapSample mapSample = new MapSample();
                                    mapSample.sample(context.getSource() ,x1, z1, x2, z2, Double.NEGATIVE_INFINITY);

                                    return 1;
                                })

                                .then(Commands.argument("scanBeginHeight", DoubleArgumentType.doubleArg())
                                        .executes(context -> {
                                            final double x1 = Vec2Argument.getVec2(context, "pos1").x;
                                            final double z1 = Vec2Argument.getVec2(context, "pos1").y;
                                            final double x2 = Vec2Argument.getVec2(context, "pos2").x;
                                            final double z2 = Vec2Argument.getVec2(context, "pos2").y;
                                            final double height = DoubleArgumentType.getDouble(context, "scanBeginHeight");

                                            MapSample mapSample = new MapSample();
                                            mapSample.sample(context.getSource() ,x1, z1, x2, z2, height);

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
