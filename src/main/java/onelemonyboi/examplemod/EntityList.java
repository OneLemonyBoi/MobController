package onelemonyboi.examplemod;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;

public class EntityList {
    public static final RegistryObject<EntityType<SpawnEggEntity>> SpawnEgg = ModRegistry.ENTITY.register("spawn_egg", () -> EntityType.Builder.<SpawnEggEntity>create(SpawnEggEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).updateInterval(10).build("spawn_egg"));

    public static void register() {}
}
