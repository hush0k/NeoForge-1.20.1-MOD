package net.hush0k.snowman.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.hush0k.snowman.util.ChunkRandomizerManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RandomChunkCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("randomchunk")
                .executes(RandomChunkCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context){
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            ChunkRandomizerManager.toggle(player.getUUID());
            boolean enabled = ChunkRandomizerManager.isEnabled(player.getUUID());

            player.sendSystemMessage(Component.literal(
                    "Режим случайных блоков:" + (enabled ? "ВКЛ" : "ВЫКЛ")
            ));
        }
        return 1;
    }



}
