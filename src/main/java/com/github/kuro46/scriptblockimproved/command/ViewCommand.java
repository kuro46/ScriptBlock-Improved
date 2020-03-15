package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ViewCommand extends Command {

    public ViewCommand() {
        super("view", Args.empty());
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();

        if (!(sender instanceof Player)) {
            sendMessage(sender, MessageKind.ERROR, "Cannot perform this command from the console");
            return;
        }
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to view information about scripts in the block");
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            player.performCommand(String.format("sbi viewat %s %s %s %s",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()));
        });
    }
}
