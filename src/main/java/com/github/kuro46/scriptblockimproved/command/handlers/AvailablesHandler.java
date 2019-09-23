package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.trigger.Trigger;
import com.github.kuro46.scriptblockimproved.script.trigger.Triggers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class AvailablesHandler extends CommandHandler {

    private final OptionHandlers handlers;
    private final Triggers triggers;

    public AvailablesHandler(final OptionHandlers handlers, final Triggers triggers) {
        super(Args.empty());

        this.handlers = Objects.requireNonNull(handlers, "'handlers' cannot be null");
        this.triggers = Objects.requireNonNull(triggers, "'triggers' cannot be null");
    }

    @Override
    public void execute(
            final CommandManager manager,
            final CommandSender sender,
            final ParsedArgs empty) {
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
        final ImmutableList<Trigger> triggers = this.triggers.getTriggers();
        if (triggers.isEmpty()) {
            sendMessage(sender, "No available triggers exist");
        } else {
            sendMessage(sender, "Available triggers:");
            triggers.forEach(trigger -> sendMessage(sender, "  " + trigger.getName()));
        }
    }
}
