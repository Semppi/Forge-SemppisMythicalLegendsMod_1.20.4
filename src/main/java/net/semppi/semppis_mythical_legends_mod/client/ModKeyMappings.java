package net.semppi.semppis_mythical_legends_mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = SemppisMythicalLegendsMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = net.minecraftforge.api.distmarker.Dist.CLIENT
)
public final class ModKeyMappings {

    public static final String CATEGORY =
            "key.categories.semppis_mythical_legends_mod";

    public static final String OPEN_INTERACTION_SCREEN =
            "key.semppis_mythical_legends_mod.open_interaction_screen";

    public static final String OPEN_TEST_MAP =
            "key.semppis_mythical_legends_mod.open_test_map";

    public static final KeyMapping INTERACTION_SCREEN =
            new KeyMapping(
                    OPEN_INTERACTION_SCREEN,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    CATEGORY
            );

    public static final KeyMapping TEST_MAP =
            new KeyMapping(
                    OPEN_TEST_MAP,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_P,
                    CATEGORY
            );

    private ModKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(INTERACTION_SCREEN);
        event.register(TEST_MAP);
    }
}
