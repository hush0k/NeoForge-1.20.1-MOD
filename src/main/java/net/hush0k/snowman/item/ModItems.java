package net.hush0k.snowman.item;

import net.hush0k.snowman.Snowman;
import net.hush0k.snowman.item.custom.BrickItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Snowman.MOD_ID);

    // Пример регистрации обычного предмета
    // public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
    //         () -> new Item(new Item.Properties()));

    // Кастомный предмет с логикой
    public static final RegistryObject<Item> BRICK = ITEMS.register("brick",
            () -> new BrickItem(new Item.Properties().durability(32)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}