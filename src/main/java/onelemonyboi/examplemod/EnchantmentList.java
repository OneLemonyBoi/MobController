package onelemonyboi.examplemod;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;

public class EnchantmentList {
    public static final RegistryObject<Enchantment> Coins = ModRegistry.ENCHANTMENT.register("coin_looting", CoinLootingEnchantment::new);

    public static void register() {}
}
