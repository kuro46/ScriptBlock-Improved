package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlerMap;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerRegistry;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class AvailablesCommand extends Command {

    @NonNull
    private final OptionHandlerMap handlers;
    @NonNull
    private final TriggerRegistry triggerRegistry;

    public AvailablesCommand() {
        super("availables", Args.empty());
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        this.handlers = sbi.getOptionHandlers();
        this.triggerRegistry = sbi.getTriggerRegistry();
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        options(sender);
        triggers(sender);
    }

    private void options(final CommandSender sender) {
        final ImmutableSet<OptionName> names = handlers.names();
        if (names.isEmpty()) {
            sendMessage(sender, "No available options exist");
        } else {
            sendMessage(sender, "Available options:");
            names.forEach(optionName -> sendMessage(sender, "  " + optionName));
        }
    }

    private void triggers(final CommandSender sender) {
        final ImmutableSet<TriggerName> triggers = triggerRegistry.getView().keySet();
        if (triggers.isEmpty()) {
            sendMessage(sender, "No available triggers exist");
        } else {
            sendMessage(sender, "Available triggers:");
            triggers.forEach(trigger -> sendMessage(sender, "  " + trigger));
        }
    }
}
