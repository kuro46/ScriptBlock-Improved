package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.CommandSection;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        manager.asMap().values()
            .forEach(command -> describeCommand(sender, new ArrayList<>(), command));
    }

    private void describeCommand(
            final CommandSender sender,
            final List<CommandSection> leftSections,
            final Command command) {
        leftSections.add(command.getSection());

        final String sectionStr = leftSections.stream()
            .map(CommandSection::getName)
            .collect(Collectors.joining(" "));

        String args = String.format("%s", command.getHandler().getArgs());
        args = (args.isEmpty() ? "" : " ") + args;

        final String message = String.format(
                "/%s%s - %s",
                sectionStr,
                args,
                command.getDescription());
        sendMessage(sender, message);

        command.getChildren().values()
            .forEach(child -> describeCommand(sender, new ArrayList<>(leftSections), child));
    }
}
