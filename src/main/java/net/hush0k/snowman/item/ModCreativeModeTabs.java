package net.hush0k.snowman.item;

import net.hush0k.snowman.Snowman;
import net.hush0k.snowman.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Snowman.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SNOWMAN_ITEMS_TAB = CREATIVE_MODE_TABS.register("snowman_items_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BRICK.get()))
                    .title(Component.translatable("creativetab.snowman.snowman_items"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.BRICK.get());
                        output.accept(ModItems.ICE_ARROW.get());

                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> SNOWMAN_BLOCKS_TAB = CREATIVE_MODE_TABS.register("snowman_blocks_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.SNOW_TNT.get()))
                    .withTabsBefore(SNOWMAN_ITEMS_TAB.getId())
                    .title(Component.translatable("creativetab.snowman.snowman_blocks"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.SNOW_TNT.get());
                        // Добавляй сюда другие блоки
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}