package net.hush0k.snowman.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChunkRandomizerManager {
    private static final Map<UUID, Boolean> ENABLED_PLAYER = new HashMap<>();

    public static void toggle(UUID playerId) {
        ENABLED_PLAYER.put(playerId, !isEnabled(playerId));
    }

    public static boolean isEnabled(UUID playerId) {
        return ENABLED_PLAYER.getOrDefault(playerId, false);
    }

}
