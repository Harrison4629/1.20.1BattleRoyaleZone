package net.harrison.battleroyalezone.events;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.data.MapData;
import net.harrison.battleroyalezone.events.customEvents.ZoneStageEvent;
import net.harrison.battleroyalezone.events.customEvents.ZoneStateEnum;
import net.harrison.battleroyalezone.mixin.IMapItemSavedDataMixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneMapDataUpdateEvent {

    private static Vec3 zoneCenter = Vec3.ZERO;
    private static Vec3 nextZoneCenter = Vec3.ZERO;
    private static int zoneLength = 0;
    private static int nextZoneLength = 0;
    private static double scale = 1.0F;
    private static boolean running = false;


    @SubscribeEvent
    public static void onZoneStage(ZoneStageEvent event) {

        if (!event.getRunningState()) {
            zoneLength = 0;
            nextZoneLength = 0;
            zoneCenter = Vec3.ZERO;
            nextZoneCenter = Vec3.ZERO;
            scale = 1.0F;
            running = false;
            return;
        }

        running = true;

        zoneLength = event.getCurrentZoneSize();
        scale = (double) zoneLength / 128;
        nextZoneCenter = event.getOffsetCenter();
        nextZoneLength = event.getFutureZoneSize();

        ZoneStateEnum state = event.getState();
        switch (state) {
            case IDLE, WARNING -> zoneCenter = event.getZoneCenter();
            case SHRINKING -> zoneCenter = event.getCurrentCenter();
            default -> throw new UnsupportedOperationException("It should not happened!");

        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level().isClientSide || event.phase == TickEvent.Phase.START) {
            return;
        }
        if (event.player.level().getGameTime() %5 == 0) {
            if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
                Player player = event.player;
                ItemStack heldItem = player.getMainHandItem();
                ItemStack lHeldItem = player.getOffhandItem();

                if (heldItem.is(Items.FILLED_MAP)) {
                    updateMapData(heldItem, (ServerLevel) player.level(), player);
                }

                if (lHeldItem.is(Items.FILLED_MAP)) {
                    updateMapData(lHeldItem, (ServerLevel) player.level(), player);
                }
            }

        }
    }

    private static void updateMapData(ItemStack heldItem, ServerLevel level, Player player) {

        Integer mapId = MapItem.getMapId(heldItem);
        if (mapId == null) return;

        MapItemSavedData oldMapData = MapItem.getSavedData(mapId, level);
        if (oldMapData == null) return;

        MapItemSavedData newMapData = IMapItemSavedDataMixin.create_Map_Data(
                (int) Math.round(player.getX()),
                (int) Math.round(player.getZ()),
                oldMapData.scale,
                true,
                true,
                oldMapData.locked,
                oldMapData.dimension
        );


        byte[] color = new byte[16384];
        MapData.drawBackGround(color, player.getX(), player.getZ(), scale);

        if (running) {
            MapData.drawNextSafeZone(color, player.getX(), player.getZ(), nextZoneCenter.x, nextZoneCenter.z, nextZoneLength, scale);
            MapData.drawUnsafeZone(color, player.getX(), player.getZ(), zoneCenter.x, zoneCenter.z, zoneLength, scale);
        }



        System.arraycopy(color, 0, newMapData.colors, 0, oldMapData.colors.length);

        DimensionDataStorage dataStorage = level.getDataStorage();
        dataStorage.set(MapItem.makeKey(mapId), newMapData);
    }

    public static Vec3 getZoneCenter() {
        return zoneCenter;
    }

    public static int getZoneLength() {
        return zoneLength;
    }

    public static int getNextZoneLength() {
        return nextZoneLength;
    }

    public static Vec3 getNextZoneCenter() {
        return nextZoneCenter;
    }
}
