package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.entity.Player;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

public final class RemoveCommand {

    @Executor(command = "sbi remove", description = "TODO")
    public void execute(final ExecutionData data) {
        final Player player = data.getSenderAsPlayer();
        if (player == null) {
            MessageUtils.sendMessage(data.getSender(), MessageKind.ERROR, "Cannot perform this command from the console");
            return;
        }
        MessageUtils.sendMessage(data.getSender(), "Click any block to remove scripts from the block");
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            final BlockPosition position = BlockPosition.ofLocation(location);
            player.performCommand(String.format("sbi removeat %s %s %s %s",
                position.getWorld(),
                position.getX(),
                position.getY(),
                position.getZ()));
        });
    }
}
