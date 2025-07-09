package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.config.MapConfig;
import net.harrison.battleroyalezone.config.ZoneConfig;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.harrison.battleroyalezone.mixin.MapItemSavedDataMixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneMapUpdateEvent {


    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {


        if (!event.getRunningState()) {
            MapConfig.zoneLength = 0;
            return;
        }

        ZoneStateEnum state =  event.getState();

        switch (state) {
            case IDLE:
                break;

            case WARNING:
                upDateIniZoneMap(event);
                break;

            case SHRINKING:
                upDateShrinkingZoneMap(event);
                break;

            default:
                break;
        }
    }

    private static void upDateIniZoneMap(ZoneStageEvent event) {
        if (event.getStage() == 0) {
            MapConfig.zoneCenter = event.getZoneCenter();
            MapConfig.zoneLength = ZoneConfig.getIniZoneSize();
        }
    }

    private static void upDateShrinkingZoneMap(ZoneStageEvent event) {
        int preZoneLength = event.getStage() > 0 ? ZoneConfig.getZoneSize(event.getStage() - 1) : ZoneConfig.getIniZoneSize();
        int futureZoneLength = ZoneConfig.getZoneSize(event.getStage());
        MapConfig.zoneLength = futureZoneLength + (preZoneLength - futureZoneLength) * event.getStateLeftTicks() / (ZoneConfig.getShrinkTick(event.getStage()));
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide && event.player.tickCount % 20 == 0) {
            Player player = event.player;
            ItemStack heldItem = player.getMainHandItem();
            ItemStack lHeldItem = player.getOffhandItem();

            if (heldItem.is(Items.FILLED_MAP)) {
                moveCenter(heldItem, (ServerLevel) player.level(), player);
            }

            if (lHeldItem.is(Items.FILLED_MAP)) {
                moveCenter(lHeldItem, (ServerLevel) player.level(), player);
            }
        }
    }

    private static void moveCenter(ItemStack heldItem, ServerLevel level, Player player) {

        Integer mapId = MapItem.getMapId(heldItem);
        if (mapId == null) return;

        MapItemSavedData oldMapData = MapItem.getSavedData(mapId, level);
        if (oldMapData == null) return;

        double distanceSq = player.distanceToSqr(oldMapData.centerX, player.getY(), oldMapData.centerZ);
        if (distanceSq > 20 * 20) {

            MapItemSavedData newMapData = MapItemSavedDataMixin.create_Map_Data(
                    (int) Math.round(player.getX()),
                    (int) Math.round(player.getZ()),
                    oldMapData.scale,
                    true,
                    true,
                    oldMapData.locked,
                    oldMapData.dimension
            );

            System.arraycopy(oldMapData.colors, 0, newMapData.colors, 0, oldMapData.colors.length);

            DimensionDataStorage dataStorage = level.getDataStorage();
            dataStorage.set(MapItem.makeKey(mapId), newMapData);
            //newMapData.setDirty();
        }
    }
}
