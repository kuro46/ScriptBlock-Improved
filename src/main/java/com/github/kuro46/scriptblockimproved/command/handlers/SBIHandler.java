package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandSection;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
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
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();

        final String free = args.getOrNull("");
        if (free == null) {
            final String version = ScriptBlockImproved.getInstance()
                .getPlugin().getDescription().getVersion();
            sendMessage(sender, "ScriptBlock-Improved v" + version);
            // TODO: improve
            Bukkit.dispatchCommand(sender, "sbi help");
        } else {
            sendMessage(sender, "Unknown command. Available commands:");
            final String subCommands =
                data.getRoot().getRootCommand().getChildren().values().stream()
                    .map(Command::getSection)
                    .map(CommandSection::getName)
                    .collect(Collectors.joining(", "));
            sendMessage(sender, subCommands);
            sendMessage(sender, "'/sbi help' for more details");
        }
    }
}
