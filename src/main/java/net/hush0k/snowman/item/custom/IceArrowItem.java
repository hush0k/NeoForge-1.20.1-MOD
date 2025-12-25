package net.hush0k.snowman.item.custom;

import net.hush0k.snowman.entity.custom.IceArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IceArrowItem extends ArrowItem {
    public IceArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter) {
        return new IceArrowEntity(level, shooter);
    }
}