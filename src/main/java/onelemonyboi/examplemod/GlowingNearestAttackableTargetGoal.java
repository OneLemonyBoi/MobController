package onelemonyboi.examplemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GlowingNearestAttackableTargetGoal<T extends LivingEntity> extends TargetGoal {
   protected final Class<T> targetClass;
   protected final int targetChance;
   protected LivingEntity nearestTarget;
   /** This filter is applied to the Entity search. Only matching entities will be targeted. */
   protected EntityPredicate targetEntitySelector;

   public GlowingNearestAttackableTargetGoal(MobEntity goalOwnerIn, Class<T> targetClassIn, boolean checkSight) {
      this(goalOwnerIn, targetClassIn, checkSight, false);
   }

   public GlowingNearestAttackableTargetGoal(MobEntity goalOwnerIn, Class<T> targetClassIn, boolean checkSight, boolean nearbyOnlyIn) {
      this(goalOwnerIn, targetClassIn, 1, checkSight, nearbyOnlyIn, (Predicate<LivingEntity>)null);
   }

   public GlowingNearestAttackableTargetGoal(MobEntity goalOwnerIn, Class<T> targetClassIn, int targetChanceIn, boolean checkSight, boolean nearbyOnlyIn, @Nullable Predicate<LivingEntity> targetPredicate) {
      super(goalOwnerIn, checkSight, nearbyOnlyIn);
      this.targetClass = targetClassIn;
      this.targetChance = targetChanceIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
      this.targetEntitySelector = (new EntityPredicate()).setDistance(this.getTargetDistance()).setCustomPredicate(targetPredicate);
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      if (this.goalOwner.getActivePotionEffect(Effects.GLOWING) == null) {
         return false;
      }
      else {
         this.findNearestTarget();
         return this.nearestTarget != null;
      }
   }

   protected AxisAlignedBB getTargetableArea(double targetDistance) {
      return this.goalOwner.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
   }

   protected void findNearestTarget() {
      List<UUID> claimedPlayers = this.goalOwner.getTags().stream().filter(GlowingNearestAttackableTargetGoal::UUIDCalc).map(UUID::fromString).collect(Collectors.toList());
      if (claimedPlayers.size() > 0 && this.goalOwner.world.getPlayerByUuid(claimedPlayers.get(0)) != null) {
         LivingEntity attacking = this.goalOwner.world.getPlayerByUuid(claimedPlayers.get(0)).getAttackingEntity();
         LivingEntity attacked = this.goalOwner.world.getPlayerByUuid(claimedPlayers.get(0)).getLastAttackedEntity();
         LivingEntity closestEntity = getClosestEntity(this.targetClass, this.targetEntitySelector, this.goalOwner, this.goalOwner.getPosX(), this.goalOwner.getPosYEye(), this.goalOwner.getPosZ(), this.getTargetableArea(this.getTargetDistance()));

         if (attacking != null && attacking.getActivePotionEffect(Effects.GLOWING) == null) {
            this.nearestTarget = attacking;
         }
         else if (attacked != null && attacked.getActivePotionEffect(Effects.GLOWING) == null) {
            this.nearestTarget = attacked;
         }
         else {
            this.nearestTarget = closestEntity;
         }
      }
      else {
         this.nearestTarget = getClosestEntity(this.targetClass, this.targetEntitySelector, this.goalOwner, this.goalOwner.getPosX(), this.goalOwner.getPosYEye(), this.goalOwner.getPosZ(), this.getTargetableArea(this.getTargetDistance()));
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.goalOwner.setAttackTarget(this.nearestTarget);
      super.startExecuting();
   }

   public void setNearestTarget(@Nullable LivingEntity target) {
      this.nearestTarget = target;
   }

   @Nullable
   <T extends LivingEntity> T getClosestEntity(Class<? extends T> entityClazz, EntityPredicate entityPredicate, @Nullable LivingEntity entity, double x, double y, double z, AxisAlignedBB p_225318_10_) {
      return this.getClosestEntity(this.goalOwner.world.getLoadedEntitiesWithinAABB(entityClazz, p_225318_10_, null), entityPredicate, entity, x, y, z);
   }

   @Nullable
   <T extends LivingEntity> T getClosestEntity(List<? extends T> entities, EntityPredicate predicate, @Nullable LivingEntity target, double x, double y, double z) {
      double d0 = -1.0D;
      T t = null;

      for(T t1 : entities) {
         if (t1.getActivePotionEffect(Effects.GLOWING) != null) {
            continue;
         }
         if (!this.goalOwner.getTags().contains(t1.getUniqueID())) {
            double d1 = t1.getDistanceSq(x, y, z);
            if (d0 == -1.0D || d1 < d0) {
               d0 = d1;
               t = t1;
            }
         }
      }

      return t;
   }

   public static boolean UUIDCalc(String name) {
      String[] components = name.split("-");
      if (components.length != 5)
         return false;
      for (int i=0; i<5; i++)
         components[i] = "0x"+components[i];

      long mostSigBits = Long.decode(components[0]).longValue();
      mostSigBits <<= 16;
      mostSigBits |= Long.decode(components[1]).longValue();
      mostSigBits <<= 16;
      mostSigBits |= Long.decode(components[2]).longValue();

      long leastSigBits = Long.decode(components[3]).longValue();
      leastSigBits <<= 48;
      leastSigBits |= Long.decode(components[4]).longValue();

      return true;
   }
}
