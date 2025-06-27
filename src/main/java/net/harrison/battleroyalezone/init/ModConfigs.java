package net.harrison.battleroyalezone.init;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.Arrays;
import java.util.List;

public class ModConfigs {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.ConfigValue<Integer> iniZoneSize;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> zoneSizes;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> zoneWarningTicks;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> zoneShrinkTicks;

    static {

        BUILDER.push("大逃杀毒圈配置(注意:三个列表长度应相等)");


        iniZoneSize = BUILDER.comment("游戏开始时的圈大小(应能包住整个地图)")
                .define("initial ZoneSize", 600);

        zoneSizes = BUILDER.comment("每个阶段安全区的大小 (从左到右递减)")
                .defineListAllowEmpty("ZoneSizes",
                        () -> Arrays.asList(400, 250, 150, 80, 40, 10),
                        obj -> obj instanceof Integer && (Integer)obj >= 1);

        zoneWarningTicks = BUILDER.comment("每个阶段安全区收缩前的警告时间 (刻，20刻=1秒)")
                .defineListAllowEmpty("ZoneWarningTicks",
                        () -> Arrays.asList(1200, 1200, 1200, 900, 600, 400),
                        obj -> obj instanceof Integer && (Integer)obj >= 0);

        zoneShrinkTicks = BUILDER.comment("每个阶段安全区收缩所需的时间 (刻，20刻=1秒)")
                .defineListAllowEmpty("ZoneShrinkTicks",
                        () -> Arrays.asList(2400, 2400, 1800, 1200, 600, 400),
                        obj -> obj instanceof Integer && (Integer)obj >= 0);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
