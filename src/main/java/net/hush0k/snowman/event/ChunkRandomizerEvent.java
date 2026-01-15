package net.hush0k.snowman.event;


import net.hush0k.snowman.Snowman;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Snowman.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChunkRandomizerEvent {
    private static final Map<UUID, ChunkPos> LAST_CHUCK = new HashMap<>();



}
