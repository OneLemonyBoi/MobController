package onelemonyboi.examplemod;

import net.minecraft.advancements.criterion.PlayerEntityInteractionTrigger;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClientStuff {
    public static void onAttack(LivingSetAttackTargetEvent event) {
        List<UUID> claimedPlayers = event.getEntityLiving().getTags().stream().filter(GlowingNearestAttackableTargetGoal::UUIDCalc).map(UUID::fromString).collect(Collectors.toList());

        if (claimedPlayers.size() == 0) {
            return;
        }

        if (event.getTarget() instanceof PlayerEntity && claimedPlayers.contains(event.getTarget().getUniqueID())) {
            event.getEntityLiving().setRevengeTarget(null);
            ((MobEntity) event.getEntityLiving()).setAttackTarget(null);
        }
    }

    public static void onEgg(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getSpawnReason() == SpawnReason.SPAWN_EGG) {
            event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.GLOWING, 10000000));
            event.getEntityLiving().addTag(event.getEntityLiving().getUniqueID().toString());
        }
    }

    public static void onSpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof CreatureEntity) {
            MobEntity entity = ((CreatureEntity) event.getEntity());
            entity.targetSelector.addGoal(0, new GlowingNearestAttackableTargetGoal<CreatureEntity>(entity, CreatureEntity.class, true));
            entity.goalSelector.addGoal(1, new TemptGoal(((CreatureEntity) event.getEntity()), 2D, Ingredient.fromItems(ItemList.FollowStick.get()), false));
        }
    }
}
