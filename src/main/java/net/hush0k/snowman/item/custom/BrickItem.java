package net.hush0k.snowman.item.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class BrickItem extends Item {
    private static final Map<Block, Block> BRICK_MAP = Map.of(
            Blocks.STONE, Blocks.DIAMOND_BLOCK,
            Blocks.END_STONE, Blocks.END_STONE_BRICKS,
            Blocks.DEEPSLATE, Blocks.DEEPSLATE_BRICKS
    );

    public BrickItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Block clickedBlock = level.getBlockState(pContext.getClickedPos()).getBlock();

        if (BRICK_MAP.containsKey(clickedBlock)) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pContext.getClickedPos(), BRICK_MAP.get(clickedBlock).defaultBlockState());

                pContext.getItemInHand().hurtAndBreak(1, ((ServerPlayer) pContext.getPlayer()),
                        item -> pContext.getPlayer().broadcastBreakEvent(EquipmentSlot.MAINHAND));

                level.playSound(null, pContext.getClickedPos(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}