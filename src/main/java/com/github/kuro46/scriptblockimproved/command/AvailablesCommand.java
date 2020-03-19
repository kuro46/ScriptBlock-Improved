package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.Trigger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class AvailablesCommand extends Command {

    public AvailablesCommand() {
        super("availables", Args.empty());
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        options(sender);
        triggers(sender);
    }

    private void options(final CommandSender sender) {
        final Set<String> names = ScriptBlockImproved.getInstance().getScriptHandler().getHandlers().keySet();
        if (names.isEmpty()) {
            sendMessage(sender, "No available options exists");
        } else {
            sendMessage(sender, "Available options:");
            names.forEach(optionName -> sendMessage(sender, "  " + optionName));
        }
    }

    private void triggers(final CommandSender sender) {
        final List<String> names = ScriptBlockImproved.getInstance().getTriggerRegistry().getTriggers().stream()
            .map(Trigger::toString)
            .collect(Collectors.toList());
        if (names.isEmpty()) {
            sendMessage(sender, "No available trigger exists");
        } else {
            sendMessage(sender, "Available triggers:");
            names.forEach(name -> sendMessage(sender, "  " + name));
        }
    }
}
