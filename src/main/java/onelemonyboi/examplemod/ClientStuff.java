package onelemonyboi.examplemod;

import com.mrcrayfish.guns.item.GunItem;
import com.oblivioussp.spartanweaponry.entity.projectile.TomahawkEntity;
import com.oblivioussp.spartanweaponry.item.SwordBaseItem;
import com.tm.calemiutils.init.InitItems;
import ml.northwestwind.potatocannon.items.PotatoCannonItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;
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

    public static void onGlowingDamage(LivingHurtEvent event) {
        List<UUID> claimedPlayers = event.getEntityLiving().getTags().stream().filter(GlowingNearestAttackableTargetGoal::UUIDCalc).map(UUID::fromString).collect(Collectors.toList());

        if (event.getSource().getTrueSource() instanceof LivingEntity && claimedPlayers.contains(event.getSource().getTrueSource().getUniqueID())) {
            LivingEntity entity = (LivingEntity) event.getSource().getTrueSource();
            if (entity.getActivePotionEffect(Effects.GLOWING) != null) {
                event.setCanceled(true);
            }
        }
    }

    public static void onKill(LivingDeathEvent event) {
        if (event.getEntityLiving().getActivePotionEffect(Effects.GLOWING) != null || !(event.getSource().getTrueSource() instanceof LivingEntity)) {
            return;
        }

        ItemStack item = ((LivingEntity) event.getSource().getTrueSource()).getHeldItemMainhand();

        int coins = (int) (event.getEntityLiving().getMaxHealth() / 4);
        coins *= EnchantmentHelper.getEnchantmentLevel(EnchantmentList.Coins.get(), ((LivingEntity) event.getSource().getTrueSource()).getHeldItemMainhand()) + 1;

        if (event.getSource().getTrueSource() instanceof TomahawkEntity) {
            coins *= 2.5;
        }
        if (item.getItem() instanceof SwordBaseItem) {
            coins *= 4;
        }
        if (item.getItem() instanceof GunItem) {
            coins *= 10;
        }
        if (item.getItem() instanceof PotatoCannonItem) {
            coins *= 15;
        }

        LivingEntity entity = event.getEntityLiving();
        InventoryHelper.spawnItemStack(entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(InitItems.COIN_PENNY.get(), coins));
    }

    public static void onSpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof CreatureEntity) {
            MobEntity entity = ((CreatureEntity) event.getEntity());
            entity.targetSelector.addGoal(0, new GlowingNearestAttackableTargetGoal<>(entity, CreatureEntity.class, true));
            entity.goalSelector.addGoal(1, new GlowingTemptGoal(((CreatureEntity) event.getEntity()),1D, Ingredient.fromItems(ItemList.FollowStick.get()), false));
        }
    }

    public static void onThrow(PlayerInteractEvent.RightClickItem event) {
        PlayerEntity player = event.getPlayer();
        World world = event.getWorld();

        ItemStack itemstack = player.getHeldItem(event.getHand());

        if (!(itemstack.getItem() instanceof SpawnEggItem)) {
            return;
        }

        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (event.getWorld().rand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote) {
            SpawnEggEntity eggentity = new SpawnEggEntity(world, player);
            eggentity.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            eggentity.setItem(player.getHeldItem(event.getHand()));
            world.addEntity(eggentity);
        }

        player.addStat(Stats.ITEM_USED.get(itemstack.getItem()));
        if (!player.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }

        if (world.isRemote) {
            event.setCancellationResult(ActionResultType.SUCCESS);
        }

        event.setCancellationResult(ActionResultType.func_233537_a_(world.isRemote));
        event.setCanceled(true);
    }
}
