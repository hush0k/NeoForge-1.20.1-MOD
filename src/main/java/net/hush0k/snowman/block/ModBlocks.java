package net.hush0k.snowman.block;

import net.hush0k.snowman.Snowman;
import net.hush0k.snowman.block.custom.SnowTNT;
import net.hush0k.snowman.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Snowman.MOD_ID);

    // Пример регистрации обычного блока
    // public static final RegistryObject<Block> EXAMPLE_BLOCK = registerBlock("example_block",
    //         () -> new Block(BlockBehaviour.Properties.of()
    //                 .strength(2F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    // Кастомный блок
    public static final RegistryObject<Block> SNOW_TNT = registerBlock("snow_tnt",
            () -> new SnowTNT(BlockBehaviour.Properties.of()
                    .strength(2F).sound(SoundType.SNOW).noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}