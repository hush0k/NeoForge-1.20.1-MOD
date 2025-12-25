package net.hush0k.snowman.entity.custom;

import net.hush0k.snowman.entity.ModEntities;
import net.hush0k.snowman.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class IceArrowEntity extends AbstractArrow {
    private int spiralStep = 0;
    private int tickCounter = 0;

    public IceArrowEntity(EntityType<? extends IceArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public IceArrowEntity(Level level, LivingEntity shooter) {
        super(ModEntities.ICE_ARROW.get(), shooter, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            tickCounter++;

            // Урон мобам в радиусе во время полёта
            damageNearbyEntities();

            // Ледяной след каждый тик
            createIceTrail();

            // Частицы снега
            spawnSnowParticles();
        }
    }

    private void damageNearbyEntities() {
        AABB area = new AABB(this.blockPosition()).inflate(5.0);
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity entity : entities) {
            if (entity == this.getOwner()) continue;

            // Белые лисицы и белые медведи - исключения
            if (entity instanceof Fox fox && fox.getVariant() == Fox.Type.SNOW) continue;
            if (entity instanceof PolarBear) continue;

            double distance = this.distanceTo(entity);
            if (distance <= 5.0) {
                if (entity instanceof Player player) {
                    player.hurt(level().damageSources().arrow(this, this.getOwner()), 4.0F);
                    Vec3 knockback = entity.position().subtract(this.position()).normalize().scale(0.5);
                    entity.setDeltaMovement(entity.getDeltaMovement().add(knockback.x, 0.3, knockback.z));
                } else if (entity instanceof IronGolem) {
                    entity.hurt(level().damageSources().arrow(this, this.getOwner()), 35.0F);
                } else if (entity instanceof Enemy) {
                    entity.hurt(level().damageSources().arrow(this, this.getOwner()), 30.0F);
                } else {
                    // Дружелюбные мобы умирают
                    entity.hurt(level().damageSources().arrow(this, this.getOwner()), 1000.0F);
                }
            }
        }
    }

    private void createIceTrail() {
        Vec3 pos = this.position();
        BlockPos basePos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);

        // Спираль по часовой стрелке: ЛВ → ПВ → ПН → ЛН
        int[][] offsets = {
                {-1, 0, -1, 0},  // Левый верхний (x-1, z-1) и (x-1, z) и (x, z-1) и (x, z)
                {0, 1, -1, 0},   // Правый верхний
                {0, 1, 0, 1},    // Правый нижний
                {-1, 0, 0, 1}    // Левый нижний
        };

        int step = spiralStep % 4;
        int[] offset = offsets[step];

        // Ставим блок 2x2
        for (int dx = offset[0]; dx <= offset[1]; dx++) {
            for (int dz = offset[2]; dz <= offset[3]; dz++) {
                BlockPos icePos = basePos.offset(dx, 0, dz);
                if (level().getBlockState(icePos).isAir() || level().getBlockState(icePos).is(Blocks.WATER)) {
                    level().setBlock(icePos, Blocks.ICE.defaultBlockState(), 3);
                }
            }
        }

        spiralStep++;
    }

    private void spawnSnowParticles() {
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    this.getX(), this.getY(), this.getZ(),
                    3,
                    0.2, 0.2, 0.2,
                    0.01
            );
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() instanceof LivingEntity target) {
            // Прямое попадание - 70 урона
            target.hurt(level().damageSources().arrow(this, this.getOwner()), 70.0F);
        }
        super.onHitEntity(result);
        onImpact();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        onImpact();
    }

    private void onImpact() {
        if (level().isClientSide) return;

        BlockPos impactPos = this.blockPosition();
        ServerLevel serverLevel = (ServerLevel) level();

        // Молния
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
        if (lightning != null) {
            lightning.moveTo(Vec3.atBottomCenterOf(impactPos));
            serverLevel.addFreshEntity(lightning);
        }

        // Заморозка области 5 блоков радиус
        freezeArea(impactPos, 5);

        // Заполнение льдом 5x5x3 (80% заполнения)
        fillWithIce(impactPos, 5, 3);

        this.discard();
    }

    private void freezeArea(BlockPos center, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 5; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level().getBlockState(pos);

                    // Превращаем траву в снежную землю
                    if (state.is(Blocks.GRASS_BLOCK)) {
                        level().setBlock(pos, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                    }

                    // Деревья в ели
                    if (isLog(state)) {
                        level().setBlock(pos, Blocks.SPRUCE_LOG.defaultBlockState(), 3);
                    }
                    if (isLeaves(state)) {
                        level().setBlock(pos, Blocks.SPRUCE_LEAVES.defaultBlockState(), 3);
                    }

                    // Вода в лёд
                    if (state.is(Blocks.WATER)) {
                        level().setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Убиваем мобов в радиусе
        AABB area = new AABB(center).inflate(radius);
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity entity : entities) {
            if (entity instanceof Fox fox && fox.getVariant() == Fox.Type.SNOW) continue;
            if (entity instanceof PolarBear) continue;

            if (entity instanceof Player player) {
                float currentHealth = player.getHealth();
                if (currentHealth > 3.0F) {
                    player.setHealth(3.0F);
                }
            } else {
                entity.hurt(level().damageSources().freeze(), 1000.0F);
            }
        }
    }

    private void fillWithIce(BlockPos center, int size, int height) {
        int halfSize = size / 2;

        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                for (int y = 0; y < height; y++) {
                    // 80% шанс заполнения
                    if (Math.random() < 0.8) {
                        BlockPos pos = center.offset(x, y, z);
                        if (!level().getBlockState(pos).is(Blocks.BEDROCK)) {
                            level().setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    private boolean isLog(BlockState state) {
        return state.is(Blocks.OAK_LOG) || state.is(Blocks.BIRCH_LOG) ||
                state.is(Blocks.SPRUCE_LOG) || state.is(Blocks.JUNGLE_LOG) ||
                state.is(Blocks.ACACIA_LOG) || state.is(Blocks.DARK_OAK_LOG) ||
                state.is(Blocks.MANGROVE_LOG) || state.is(Blocks.CHERRY_LOG);
    }

    private boolean isLeaves(BlockState state) {
        return state.is(Blocks.OAK_LEAVES) || state.is(Blocks.BIRCH_LEAVES) ||
                state.is(Blocks.SPRUCE_LEAVES) || state.is(Blocks.JUNGLE_LEAVES) ||
                state.is(Blocks.ACACIA_LEAVES) || state.is(Blocks.DARK_OAK_LEAVES) ||
                state.is(Blocks.MANGROVE_LEAVES) || state.is(Blocks.CHERRY_LEAVES) ||
                state.is(Blocks.AZALEA_LEAVES) || state.is(Blocks.FLOWERING_AZALEA_LEAVES);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.ICE_ARROW.get());
    }
}