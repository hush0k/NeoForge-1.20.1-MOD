package net.hush0k.snowman.entity.client;

import net.hush0k.snowman.Snowman;
import net.hush0k.snowman.entity.custom.IceArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class IceArrowRenderer extends ArrowRenderer<IceArrowEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Snowman.MOD_ID, "textures/entity/projectiles/ice_arrow.png");

    public IceArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IceArrowEntity entity) {
        return TEXTURE;
    }
}