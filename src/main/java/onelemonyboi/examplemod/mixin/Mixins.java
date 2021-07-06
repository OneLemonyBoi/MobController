package onelemonyboi.examplemod.mixin;

import com.bobmowzie.mowziesmobs.server.entity.wroughtnaut.EntityWroughtnaut;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import onelemonyboi.examplemod.ExampleMod;
import onelemonyboi.examplemod.KeyBindings;
import onelemonyboi.examplemod.SpawnEggEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;

public class Mixins {
	@Mixin(Entity.class)
	public static class Mixin1 {
		/**
		 * @author OneLemonyBoi
		 */
		@Overwrite
		public boolean isOnSameTeam(Entity entityIn) {
			return false;
		}
	}

	@Mixin(SpawnEggItem.class)
	public static abstract class Mixin2 extends Item {
		@Shadow public abstract EntityType<?> getType(@Nullable CompoundNBT nbt);

		public Mixin2(Properties properties) {
			super(properties);
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
			ItemStack itemstack = playerIn.getHeldItem(handIn);
//			worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
//			if (!worldIn.isRemote) {
//				SpawnEggEntity eggentity = new SpawnEggEntity(worldIn, playerIn);
//				eggentity.setDirectionAndMovement(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
//				eggentity.setItem(playerIn.getHeldItem(handIn));
//				worldIn.addEntity(eggentity);
//			}
//
//			playerIn.addStat(Stats.ITEM_USED.get(this));
//			if (!playerIn.abilities.isCreativeMode) {
//				itemstack.shrink(1);
//			}

			return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
		}

		/**
		 * @author OneLemonyBoi
		 */
		@Overwrite
		public ActionResultType onItemUse(ItemUseContext context) {
			World world = context.getWorld();
			if (!(world instanceof ServerWorld)) {
				return ActionResultType.SUCCESS;
			} else {
				ItemStack itemstack = context.getItem();
				BlockPos blockpos = context.getPos();
				Direction direction = context.getFace();
				BlockState blockstate = world.getBlockState(blockpos);
				if (blockstate.matchesBlock(Blocks.SPAWNER)) {
					TileEntity tileentity = world.getTileEntity(blockpos);
					if (tileentity instanceof MobSpawnerTileEntity) {
						AbstractSpawner abstractspawner = ((MobSpawnerTileEntity) tileentity).getSpawnerBaseLogic();
						EntityType<?> entitytype1 = this.getType(itemstack.getTag());
						abstractspawner.setEntityType(entitytype1);
						tileentity.markDirty();
						world.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
						itemstack.shrink(1);
						return ActionResultType.CONSUME;
					}
				}

				BlockPos blockpos1;
				if (blockstate.getCollisionShapeUncached(world, blockpos).isEmpty()) {
					blockpos1 = blockpos;
				} else {
					blockpos1 = blockpos.offset(direction);
				}

				EntityType<?> entitytype = this.getType(itemstack.getTag());
				Entity entity = entitytype.spawn((ServerWorld) world, itemstack, context.getPlayer(), blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
				if (entity != null) {
					if (entity instanceof LivingEntity && context.getPlayer() != null) {
						LivingEntity livingEntity = (LivingEntity) entity;
						livingEntity.addPotionEffect(new EffectInstance(Effects.GLOWING, 10000000));
						livingEntity.addTag(context.getPlayer().getUniqueID().toString());
					}
					itemstack.shrink(1);
				}

				return ActionResultType.CONSUME;
			}
		}
	}

	@Mixin(EntityWroughtnaut.class)
	public static abstract class Mixin3 {
		@Shadow public abstract void setActive(boolean isActive);

		@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", cancellable = true)
		public void isOnSameTeam(EntityType<? extends EntityWroughtnaut> type, World world, CallbackInfo ci) {
			this.setActive(true);
		}
	}

	@Mixin(WorldRenderer.class)
	public static abstract class Mixin4 {
		@Shadow @Nullable private Framebuffer entityOutlineFramebuffer;

		@Shadow @Nullable private ShaderGroup entityOutlineShader;

		@Shadow @Final private Minecraft mc;

		/**
		 * @author OneLemonyBoi
		 */
		@Overwrite
		public boolean isRenderEntityOutlines() {
			return this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.player != null && !KeyBindings.toggleGlowing.isActive();
		}
	}
}
