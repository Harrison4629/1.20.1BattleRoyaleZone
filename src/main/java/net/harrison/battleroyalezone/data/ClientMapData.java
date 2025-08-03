package net.harrison.battleroyalezone.data;

import net.harrison.battleroyalezone.Battleroyalezone;
import net.harrison.battleroyalezone.manager.LerpManager;
import net.harrison.battleroyalezone.api.IPixelSpaceOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector2d;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Battleroyalezone.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientMapData {
    private static final Map<ResourceKey<Level>, Map<Long, Byte>> MapData = new HashMap<>();

    private static double Scale = 1.0;

    private static final HashMap<String, IPixelSpaceOverlay> overlays = new HashMap<>();


    private static LerpManager manager;

    public static void receiveMapDataChunk(ResourceKey<Level> key, Map<Long, Byte> chunkData, boolean overWrite) {
        if (overWrite) {
            MapData.put(key, new ConcurrentHashMap<>());
        }
        MapData.computeIfAbsent(key, k -> new ConcurrentHashMap<>());
        MapData.get(key).putAll(chunkData);
    }

    public static byte getBackGroundColor(double worldX, double worldZ) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return 0;
        }
        Map<Long, Byte> levelMapData = MapData.get(level.dimension());
        if (levelMapData == null) {
            return 0;
        }

        int wX = (int) worldX;
        int wZ = (int) worldZ;

        if (levelMapData.containsKey(coordinatesToLong(wX, wZ))) {
            return levelMapData.get(coordinatesToLong(wX, wZ));
        } else {
            return 0;
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (manager != null) {
                manager.tick();
            }
        }
    }

    private static long coordinatesToLong(int x, int z) {
        return (long)x << 32 | z & 0xFFFFFFFFL;
    }

    public static void setManager(double previousZoneLength, double nextZoneLength, Vector2d previousZoneCenter,
                                  Vector2d nextZoneCenter, int totalTicks, boolean isShrinking) {
        manager = new LerpManager(previousZoneLength, nextZoneLength, previousZoneCenter,
                nextZoneCenter, totalTicks, isShrinking);

    }

    public static Vector2d getCurrentZoneCenter() {
        return manager.getCurrentZoneCenter();
    }

    public static double getCurrentZoneSize() {
        return manager.getCurrentZoneSize();
    }

    public static double getScale() {
        return Scale;
    }

    public static void setScale(double scale) {
        Scale = Mth.clamp(scale, 0.5, 8);
    }

    public static int applyOverlays(int initialColor, int pixelX, int pixelZ, double mapWorldX, double mapWorldZ, double scale) {
        int currentColor = initialColor;

        for (String key : overlays.keySet()) {
            currentColor = overlays.get(key).draw(currentColor, pixelX, pixelZ, mapWorldX, mapWorldZ);
        }
        return currentColor;
    }

    public static void addOverlay(IPixelSpaceOverlay overlay, String tag) {
        overlays.putIfAbsent(tag, overlay);
    }

    public static void removeOverlay(String overlayTag) {
        overlays.remove(overlayTag);
    }

    public static void clearOverlays() {
        overlays.clear();
    }

    public static boolean OverLaysIsEmpty() {
        return overlays.isEmpty();
    }

    public static double getNextZoneSize() {
        return manager.getNextZoneLength();
    }

    public static Vector2d getNextZoneCenter() {
        return manager.getNextZoneCenter();
    }
}
