package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.entity.Player;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

public final class ViewCommand {

    @Executor(command = "sbi view", description = "TODO")
    public void execute(final ExecutionData data) {
        final Player player = data.getSenderAsPlayer();
        if (player == null) {
            MessageUtils.sendMessage(data.getSender(), MessageKind.ERROR, "Cannot perform this command from the console");
            return;
        }
        MessageUtils.sendMessage(player, "Click any block to view information about scripts in the block");
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            player.performCommand(String.format("sbi viewat %s %s %s %s",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()));
        });
    }
}
