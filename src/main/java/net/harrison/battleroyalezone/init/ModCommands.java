package net.harrison.battleroyalezone.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.harrison.basicdevtool.init.ModMessages;
import net.harrison.basicdevtool.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.harrison.basicdevtool.util.DelayTask;
import net.harrison.battleroyalezone.data.ServerMapData;
import net.harrison.battleroyalezone.data.ZoneData;
import net.harrison.battleroyalezone.events.ZoneTicker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import java.util.Collection;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("brzone")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("start")
                        .executes(context -> {
                            ZoneTicker.startZoneSystem(context.getSource());
                            return 1;
                        })
                )

                .then(Commands.literal("info")
                        .executes(context -> {
                            MinecraftServer server = context.getSource().getServer();

                            String fullInfo = ZoneData.getInfo();
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
                            ZoneTicker.stopZoneSystem();
                            return 1;
                        })
                )
        );


        dispatcher.register(Commands.literal("sample")
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

        dispatcher.register(Commands.literal("pushmap")
                .requires(sourceStack -> sourceStack.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");

                            ServerMapData.get((ServerLevel) player.level()).pushMapData(player);
                            player.sendSystemMessage(Component.translatable("command.battleroyalezone.map_push_success"), false);
                            return 1;
                        })
                )

                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> {
                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
                            int count = 0;
                            for (ServerPlayer player : players) {

                                ServerMapData.get((ServerLevel) player.level()).pushMapData(player);
                                player.sendSystemMessage(Component.translatable("command.battleroyalezone.map_push_success"), false);
                                count++;
                            }
                            return count;
                        })
                )
        );
    }
}
