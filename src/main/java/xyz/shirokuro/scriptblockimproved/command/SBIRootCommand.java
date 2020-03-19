package xyz.shirokuro.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CommandName;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.commandutility.ParsedArgs;
import com.github.kuro46.commandutility.RootCommand;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.command.migration.MigrateCommand;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class SBIRootCommand extends RootCommand {

    private SBIRootCommand() {
        super(
            "sbi",
            Args.builder()
                .optional("")
                .build()
        );
        addChild(new HelpCommand());
        addChild(new MigrateCommand());
        addChild(new ListCommand());
        addChild(new AvailablesCommand());
        addChild(new SaveCommand());
        addChild(new PermMapCommand());
        addChild(new CreateCommand());
        addChild(new CreateAtCommand());
        addChild(new RemoveCommand());
        addChild(new RemoveAtCommand());
        addChild(new ViewCommand());
        addChild(new ViewAtCommand());
    }

    public static void register() {
        new SBIRootCommand();
    }

    @Override
    public void onParseFailed(
        @NonNull final CommandSender sender,
        @NonNull final ImmutableList<CommandName> commandPath,
        @NonNull final Command command
    ) {
        final String pathStr = commandPath.stream()
            .map(CommandName::toString)
            .collect(Collectors.joining(" "));
        MessageUtils.sendMessage(sender,
            MessageKind.ERROR,
            "Usage: /%s %s",
            pathStr,
            command.getArgs());
    }

    @Override
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();

        final String free = args.getOrNull("");
        if (free == null) {
            final String version = ScriptBlockImproved.getInstance()
                .getPlugin().getDescription().getVersion();
            MessageUtils.sendMessage(sender, "ScriptBlock-Improved v" + version);
            // TODO: improve
            Bukkit.dispatchCommand(sender, "sbi help");
        } else {
            MessageUtils.sendMessage(sender, "Unknown command.");
            final List<String> subCommands =
                data.getRoot().getChildren().values().stream()
                    .map(Command::getName)
                    .map(CommandName::toString)
                    .collect(Collectors.toList());
            final String firstArgument = Iterables.get(Splitter.on(' ').split(free), 0);
            final ExtractedResult result = FuzzySearch.extractOne(firstArgument, subCommands);
            MessageUtils.sendMessage(sender, "Did you mean '%s'? ", result.getString());
            MessageUtils.sendMessage(sender, "'/sbi help' for show all commands");
        }
    }
}
