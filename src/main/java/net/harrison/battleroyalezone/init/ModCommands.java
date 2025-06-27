package net.harrison.battleroyalezone.init;

import com.mojang.brigadier.CommandDispatcher;
import net.harrison.basicdevtool.init.ModMessages;
import net.harrison.basicdevtool.networking.s2cpacket.PlaySoundToClientS2CPacket;
import net.harrison.basicdevtool.util.DelayTask;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.ZoneStagePublisherEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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

    }
}
