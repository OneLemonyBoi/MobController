package onelemonyboi.examplemod;

import andrews.pandoras_creatures.entities.BufflonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Objects;

public class SpawnEggEntity extends ProjectileItemEntity {
    public SpawnEggEntity(EntityType<SpawnEggEntity> p_i50154_1_, World p_i50154_2_) {
        super(p_i50154_1_, p_i50154_2_);
    }

    public SpawnEggEntity(World worldIn, LivingEntity throwerIn) {
        super(EntityList.SpawnEgg.get(), throwerIn, worldIn);
    }

    public SpawnEggEntity(World worldIn, double x, double y, double z) {
        super(EntityList.SpawnEgg.get(), x, y, z, worldIn);
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.getPosX(), this.getPosY(), this.getPosZ(), ((double) this.rand.nextFloat() - 0.5D) * 0.08D, ((double) this.rand.nextFloat() - 0.5D) * 0.08D, ((double) this.rand.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            if (this.getItem().getItem() instanceof SpawnEggItem && this.getShooter() instanceof PlayerEntity) {
                SpawnEggItem item = (SpawnEggItem) this.getItem().getItem();
                Entity entity = item.getType(this.getItem().getTag()).spawn((ServerWorld)world, this.getItem(), (PlayerEntity) this.getShooter(), this.getPosition(), SpawnReason.SPAWN_EGG, true, false);

                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new EffectInstance(Effects.GLOWING, 10000000));
                    livingEntity.addTag(this.getShooter().getUniqueID().toString());
                }

                if (entity instanceof BufflonEntity) {
                    ((BufflonEntity) entity).setTamed(true);
                    ((BufflonEntity) entity).setSaddled(true);
                }
            }

            this.world.setEntityState(this, (byte)3);
            this.remove();
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.WOODEN_AXE;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.world.isRemote) {
            return;
        }

        if (world.getFluidState(getPosition()).getFluid() != Fluids.EMPTY) {
            if (this.getItem().getItem() instanceof SpawnEggItem && this.getShooter() instanceof PlayerEntity) {
                SpawnEggItem item = (SpawnEggItem) this.getItem().getItem();
                Entity entity = item.getType(this.getItem().getTag()).spawn((ServerWorld)world, this.getItem(), (PlayerEntity) this.getShooter(), this.getPosition(), SpawnReason.SPAWN_EGG, true, false);

                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.addPotionEffect(new EffectInstance(Effects.GLOWING, 10000000));
                    livingEntity.addTag(this.getShooter().getUniqueID().toString());
                }
            }

            this.world.setEntityState(this, (byte)3);
            this.remove();
        }
    }
}
