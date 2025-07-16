package net.harrison.battleroyalezone.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeyBinds {
    public static final String KEY_CATEGORY = "key.category.battleroyalezone";
    public static final String KEY_BIND_OPEN_MAP = "key.battleroyalezone.open_map";

    public static final KeyMapping OPEN_MAP = new KeyMapping(KEY_BIND_OPEN_MAP, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, KEY_CATEGORY);
}
