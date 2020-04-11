package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.command.CommandSender;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.Trigger;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class AvailablesCommand {

    @Executor(command = "sbi availables", description = "TODO")
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getSender();
        options(sender);
        triggers(sender);
    }

    private void options(final CommandSender sender) {
        final Set<String> names = ScriptBlockImproved.getInstance().getScriptHandler().getHandlers().keySet();
        if (names.isEmpty()) {
            MessageUtils.sendMessage(sender, "No available options exists");
        } else {
            MessageUtils.sendMessage(sender, "Available options:");
            names.forEach(optionName -> MessageUtils.sendMessage(sender, "  " + optionName));
        }
    }

    private void triggers(final CommandSender sender) {
        final List<String> names = ScriptBlockImproved.getInstance().getTriggerRegistry().getTriggers().stream()
            .map(Trigger::getName)
            .collect(Collectors.toList());
        if (names.isEmpty()) {
            MessageUtils.sendMessage(sender, "No available trigger exists");
        } else {
            MessageUtils.sendMessage(sender, "Available triggers:");
            names.forEach(name -> MessageUtils.sendMessage(sender, "  " + name));
        }
    }
}
