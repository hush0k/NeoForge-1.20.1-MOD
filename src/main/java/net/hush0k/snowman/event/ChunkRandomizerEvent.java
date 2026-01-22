package net.hush0k.snowman.event;


import net.hush0k.snowman.Snowman;
import net.hush0k.snowman.util.ChunkRandomizerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = Snowman.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChunkRandomizerEvent {
    // Хранит последний чанк каждого игрока
    private static final Map<UUID, ChunkPos> LAST_CHUCK = new HashMap<>();
    // ДОБАВЛЕНО: Общий Random instance для оптимизации
    private static final Random RANDOM = new Random();
    // Набор блоков для рандомизации
    private static final Set<Block> RANDOM_BLOCKS = Set.of(
            Blocks.STONE, Blocks.DIAMOND_BLOCK, Blocks.OAK_WOOD,
            Blocks.CAKE, Blocks.WATER, Blocks.CLAY, Blocks.OAK_LEAVES, Blocks.ICE, Blocks.GRASS_BLOCK,
            Blocks.END_STONE, Blocks.OBSIDIAN, Blocks.TNT, Blocks.DEEPSLATE,
            Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.ACACIA_WOOD, Blocks.CHERRY_WOOD,
            Blocks.EMERALD_BLOCK, Blocks.GOLD_BLOCK, Blocks.PLAYER_HEAD, Blocks.SAND,
            Blocks.GRAVEL
    );

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (event.player.level().isClientSide) return;

        // Проверяем, включен ли режим для этого игрока
        if (!ChunkRandomizerManager.isEnabled(player.getUUID())) return;

        ChunkPos currentChunk = player.chunkPosition();
        ChunkPos lastChunk = LAST_CHUCK.get(player.getUUID());

        // ИСПРАВЛЕНО: Если игрок в том же чанке - ничего не делаем
        if(lastChunk != null && lastChunk.equals(currentChunk)) return;

        // Обновляем последний чанк игрока
        LAST_CHUCK.put(player.getUUID(), currentChunk);

        // Рандомизируем новый чанк
        randomizeChunk(player.level(), currentChunk);
    }

    public static void randomizeChunk(Level level, ChunkPos chunkPos) {
        // Выбираем случайный блок из набора
        List<Block> blockList = new ArrayList<>(RANDOM_BLOCKS);
        Block randomBlock = blockList.get(RANDOM.nextInt(blockList.size())); // ИСПРАВЛЕНО: используем статический RANDOM
        BlockState newState = randomBlock.defaultBlockState();

        // Получаем начальные координаты чанка
        int startX = chunkPos.getMinBlockX();
        int startZ = chunkPos.getMinBlockZ();

        // Проходим по всем блокам в чанке (16x16 по XZ)
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Проходим по всей высоте мира
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
                    BlockPos pos = new BlockPos(startX + x, y, startZ + z);
                    BlockState currentState = level.getBlockState(pos);

                    // Заменяем все блоки кроме воздуха и бедрока
                    if(!currentState.isAir() && !currentState.is(Blocks.BEDROCK)) {
                        level.setBlock(pos, newState, 2);
                    }
                }
            }
        }
    }


}
