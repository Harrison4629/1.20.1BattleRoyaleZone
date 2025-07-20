package net.harrison.battleroyalezone.init;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.Arrays;
import java.util.List;

public class ModConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> iniZoneSize;
    public static final ForgeConfigSpec.ConfigValue<Double> iniZoneDamage;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> zoneSizes;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> zoneDamage;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> zoneWarningTicks;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> zoneShrinkTicks;

    static {

        BUILDER.push("大逃杀毒圈配置(注意:四个列表长度应相等，从左到右为正序)");


        iniZoneSize = BUILDER.comment("游戏开始时的圈大小(应能包住整个地图)")
                .define("initial ZoneSize", 1000);

        iniZoneDamage = BUILDER.comment("游戏开始时的圈伤害(每半秒伤害)")
                .define("initial ZoneDamage", 0.1);

        zoneSizes = BUILDER.comment("每个阶段安全区的大小")
                .defineListAllowEmpty("ZoneSizes",
                        () -> Arrays.asList(400, 180, 80, 40, 20, 10, 5, 1),
                        obj -> obj instanceof Integer && (Integer)obj >= 1);

        zoneDamage = BUILDER.comment("每个阶段安全区的伤害")
                .defineListAllowEmpty("ZoneDamage",
                        () -> Arrays.asList(0.1, 0.3, 0.4, 0.5, 0.6, 0.9, 1.2, 1.4),
                        obj -> obj instanceof Double);

        zoneWarningTicks = BUILDER.comment("每个阶段安全区收缩前的警告时间 (刻，20刻=1秒)")
                .defineListAllowEmpty("ZoneWarningTicks",
                        () -> Arrays.asList(1350, 900, 650, 600, 500, 450, 350, 300),
                        obj -> obj instanceof Integer && (Integer)obj >= 0);

        zoneShrinkTicks = BUILDER.comment("每个阶段安全区收缩所需的时间 (刻，20刻=1秒)")
                .defineListAllowEmpty("ZoneShrinkTicks",
                        () -> Arrays.asList(1600, 800, 600, 600, 600, 300, 300, 300),
                        obj -> obj instanceof Integer && (Integer)obj >= 0);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
