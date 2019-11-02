package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandSection;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.stream.Collectors;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
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
            sendMessage(sender, "Unknown command.");
            final List<String> subCommands =
                data.getRoot().getRootCommand().getChildren().values().stream()
                    .map(Command::getSection)
                    .map(CommandSection::getName)
                    .collect(Collectors.toList());
            final String firstArgument = Iterables.get(Splitter.on(' ').split(free), 0);
            final ExtractedResult result = FuzzySearch.extractOne(firstArgument, subCommands);
            sendMessage(sender, "Did you mean '%s'? ", result.getString());
            sendMessage(sender, "'/sbi help' for show all commands");
        }
    }
}
