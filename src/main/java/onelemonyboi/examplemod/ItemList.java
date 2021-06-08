package onelemonyboi.examplemod;

import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;

public class ItemList {
    public static final RegistryObject<Item> FollowStick = ModRegistry.ITEMS.register("follow_stick", () -> new Item(new Item.Properties()));

    public static void register() {}
}
