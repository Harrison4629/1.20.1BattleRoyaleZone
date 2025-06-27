package net.harrison.battleroyalezone.config;

import net.harrison.battleroyalezone.init.ModConfigs;

import java.util.List;

public class ZoneConfig {

    private static final int tick = 20;

    public static int getIniZoneSize() {
        return ModConfigs.iniZoneSize.get();
    }

    public static int getZoneSize(int stage) {
        List<? extends Integer> ZONE_SIZES = ModConfigs.zoneSizes.get();
        return ZONE_SIZES.get(stage);
    }

    public static int getWarningTick(int stage) {
        List<? extends Integer> ZONE_WARNING_TICKS = ModConfigs.zoneWarningTicks.get();
        return ZONE_WARNING_TICKS.get(stage);
    }

    public static int getShrinkTick(int stage) {
        List<? extends Integer> ZONE_SHRINK_TICKS = ModConfigs.zoneShrinkTicks.get();
        return ZONE_SHRINK_TICKS.get(stage);
    }

    public static int getMaxStage() {
        List<? extends Integer> ZONE_SIZES = ModConfigs.zoneSizes.get();
        return ZONE_SIZES.size();
    }

    public static String getInfo() {
        List<? extends Integer> ZONE_SIZES = ModConfigs.zoneSizes.get();
        List<? extends Integer> ZONE_WARNING_TICKS = ModConfigs.zoneWarningTicks.get();
        List<? extends Integer> ZONE_SHRINK_TICKS = ModConfigs.zoneShrinkTicks.get();

        StringBuilder string = new StringBuilder();

        string.append("§6大逃杀缩圈系统配置\n");

        string.append("§7-初始圈大小:§e").append(ModConfigs.iniZoneSize.get()).append("方块\n\n");

        for (int i = 0; i < ZONE_SIZES.size(); i++) {
            string.append("§7- 第").append(i+1).append("圈大小: §e")
                    .append(ZONE_SIZES.get(i)).append("方块\n");
        }
        string.append("\n");
        for (int i = 0; i < ZONE_WARNING_TICKS.size(); i++) {
            string.append("§7- 第").append(i+1).append("圈警告时间: §e")
                    .append(ZONE_WARNING_TICKS.get(i) / tick).append("秒\n");
        }
        string.append("\n");
        for (int i = 0; i < ZONE_SHRINK_TICKS.size(); i++) {
            string.append("§7- 第").append(i+1).append("圈缩圈时间: §e")
                    .append(ZONE_SHRINK_TICKS.get(i) / tick).append("秒\n");
        }

        return string.toString();

    }
}
