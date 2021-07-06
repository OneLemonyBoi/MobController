package onelemonyboi.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ActualClient {
    public static void client() {
        RenderingRegistry.registerEntityRenderingHandler(EntityList.SpawnEgg.get(), (manager) -> new SpriteRenderer(manager, Minecraft.getInstance().getItemRenderer()));

        KeyBindings.register();
    }
}