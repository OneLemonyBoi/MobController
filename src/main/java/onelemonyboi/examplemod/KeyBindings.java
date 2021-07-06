package onelemonyboi.examplemod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static ToggleKeyBinding toggleGlowing = new ToggleKeyBinding("mobcontroller.key.trigger", GLFW.GLFW_KEY_SEMICOLON, "key.categories.misc");

    public static void register() {
        ClientRegistry.registerKeyBinding(toggleGlowing);
    }
}
