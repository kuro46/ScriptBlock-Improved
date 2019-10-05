package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerRegistry;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class AvailablesHandler extends CommandHandler {

    @NonNull
    private final OptionHandlers handlers;
    @NonNull
    private final TriggerRegistry triggerRegistry;

    public AvailablesHandler() {
        super(Args.empty());
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
            names.forEach(optionName -> sendMessage(sender, "  " + optionName.getName()));
        }
    }

    private void triggers(final CommandSender sender) {
        final ImmutableSet<TriggerName> triggers = triggerRegistry.getView();
        if (triggers.isEmpty()) {
            sendMessage(sender, "No available triggers exist");
        } else {
            sendMessage(sender, "Available triggers:");
            triggers.forEach(trigger -> sendMessage(sender, "  " + trigger));
        }
    }
}
