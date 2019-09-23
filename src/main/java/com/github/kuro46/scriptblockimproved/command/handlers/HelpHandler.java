package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class HelpHandler extends CommandHandler {

    public HelpHandler() {
        super(Args.empty());
    }

    @Override
    public void execute(
            final CommandManager manager,
            final CommandSender sender,
            final ParsedArgs empty) {
        sendMessage(sender, "Usage:");
        manager.asMap().values().forEach(command -> {
            String args = String.format("%s", command.getHandler().getArgs());
            args = (args.isEmpty() ? "" : " ") + args;

            final String message = String.format(
                    "/%s%s - %s",
                    command.getName(),
                    args,
                    command.getDescription());
            sendMessage(sender, message);
        });
    }
}
