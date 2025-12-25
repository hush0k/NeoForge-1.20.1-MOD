package net.hush0k.snowman.entity;

import net.hush0k.snowman.Snowman;
import net.hush0k.snowman.entity.custom.IceArrowEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Snowman.MOD_ID);

    public static final RegistryObject<EntityType<IceArrowEntity>> ICE_ARROW =
            ENTITY_TYPES.register("ice_arrow",
                    () -> EntityType.Builder.<IceArrowEntity>of(IceArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("ice_arrow"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}