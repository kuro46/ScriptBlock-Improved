package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.CommandSection;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class SBIHandler extends CommandHandler {

    public SBIHandler() {
        super(Args.builder()
                .optional("")
                .build());
    }

    @Override
    public void execute(
            final CommandManager manager,
            final CommandSender sender,
            final ParsedArgs args) {
        final String free = args.getOrNull("");
        if (free == null) {
            //TODO: improve
            final String version = Bukkit.getPluginManager()
                .getPlugin("ScriptBlock-Improved").getDescription().getVersion();
            sendMessage(sender, "ScriptBlock-Improved v" + version);
            Bukkit.dispatchCommand(sender, "sbi help");
        } else {
            sendMessage(sender, "Unknown command. Available commands:");
            final String subCommands = manager.asMap().values().stream()
                .flatMap(root -> root.getChildren().values().stream())
                .map(Command::getSection)
                .map(CommandSection::getName)
                .collect(Collectors.joining(", "));
            sendMessage(sender, subCommands);
            sendMessage(sender, "'/sbi help' for more details");
        }
    }
}
