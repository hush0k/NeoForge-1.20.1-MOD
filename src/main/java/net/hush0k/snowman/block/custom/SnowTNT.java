package net.hush0k.snowman.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SnowTNT extends Block {
    public SnowTNT(Properties properties) {
        super(properties);
    }

    @Override
    public void onProjectileHit(Level pLevel, BlockState pState, BlockHitResult pHit, Projectile pProjectile) {
        if (!pLevel.isClientSide && pProjectile instanceof Snowball) {
            BlockPos pos = pHit.getBlockPos();

            pLevel.explode(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    20.0F,
                    Level.ExplosionInteraction.NONE
            );

            pLevel.playSound(
                    null,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    SoundEvents.GENERIC_EXPLODE,
                    SoundSource.BLOCKS,
                    2.0F,
                    1.0F
            );

            freezeLargeArea(pLevel, pos);

            if (pLevel instanceof ServerLevel serverLevel) {
                startSnowFall(serverLevel, pos);
            }

            pLevel.removeBlock(pos, false);
        }

        super.onProjectileHit(pLevel, pState, pHit, pProjectile);
    }

    private void freezeLargeArea(Level pLevel, BlockPos center) {
        int radius = 25;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -3; y <= 5; y++) {
                    BlockPos p = center.offset(x, y, z);
                    BlockState state = pLevel.getBlockState(p);

                    if (state.is(Blocks.WATER)) {
                        pLevel.setBlock(p, Blocks.ICE.defaultBlockState(), 3);
                    }

                    if (state.is(Blocks.LAVA)) {
                        pLevel.setBlock(p, Blocks.OBSIDIAN.defaultBlockState(), 3);
                    }

                    if (state.is(Blocks.GRASS_BLOCK)) {
                        pLevel.setBlock(
                                p,
                                Blocks.GRASS_BLOCK.defaultBlockState().setValue(SnowyDirtBlock.SNOWY, true),
                                3
                        );

                        BlockPos abovePos = p.above();
                        if (pLevel.getBlockState(abovePos).isAir()) {
                            pLevel.setBlock(abovePos, Blocks.SNOW.defaultBlockState(), 3);
                        }
                    }

                    if (state.is(Blocks.DIRT)) {
                        pLevel.setBlock(p, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
                    }

                    if (state.is(Blocks.STONE)) {
                        BlockPos abovePos = p.above();
                        if (pLevel.getBlockState(abovePos).isAir()) {
                            pLevel.setBlock(abovePos, Blocks.SNOW.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    private void startSnowFall(ServerLevel pLevel, BlockPos center) {
        new Thread(() -> {
            try {
                for (int i = 0; i < 5000; i++) {
                    spawnSnowParticle(pLevel, center, 25);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void spawnSnowParticle(ServerLevel pLevel, BlockPos center, int radius) {
        for (int i = 0; i < 100; i++) {
            double x = center.getX() + (Math.random() - 0.5) * radius * 2;
            double z = center.getZ() + (Math.random() - 0.5) * radius * 2;
            double y = center.getY() + 10 + Math.random() * 10;

            pLevel.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    x, y, z,
                    1,
                    0.5, 0.5, 0.5,
                    0.1
            );
        }
    }
}