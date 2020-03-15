package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.CandidateFactories;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.commandutility.ParsedArgs;
import com.github.kuro46.scriptblockimproved.Author;
import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.OptionListParser;
import com.github.kuro46.scriptblockimproved.Script;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.google.common.collect.ImmutableList;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CreateAtCommand extends Command {

    public CreateAtCommand() {
        super(
            "createAt",
            Args.builder()
                .required("world", "x", "y", "z", "trigger", "options")
                .build()
        );
    }

    @Override
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();
        // Parse options
        final List<Script.Option> options;
        try {
            final String rawOptions = args.getOrFail("options");
            options = OptionListParser.parse(rawOptions);
        } catch (final OptionListParser.ParseException e) {
            sendMessage(sender, MessageKind.ERROR, e.getMessage());
            return;
        }
        // Parse position
        final BlockPosition position = BlockPosition.parseArgs(sender, args).orElse(null);
        if (position == null) {
            return;
        }
        final String trigger = args.getOrFail("trigger");
        final Author author;
        if (sender instanceof Player) {
            author = Author.player((Player) sender);
        } else {
            author = Author.system("console");
        }
        // Build script
        final Script script = Script.builder()
            .author(author)
            .createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
            .triggerName(trigger)
            .options(ImmutableList.copyOf(options))
            .build();
        ScriptBlockImproved.getInstance().getScriptList().add(position, script);
        sendMessage(sender, MessageKind.SUCCESS, "The script has been created");
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.worlds())
            .build(data.getArgName(), data.getCurrentValue());
    }
}
