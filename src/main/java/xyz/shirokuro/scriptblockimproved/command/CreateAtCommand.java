package xyz.shirokuro.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.CandidateFactories;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.commandutility.ParsedArgs;
import xyz.shirokuro.scriptblockimproved.Author;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.OptionListParser;
import xyz.shirokuro.scriptblockimproved.Script;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.Trigger;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import com.google.common.collect.ImmutableList;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

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
        MessageUtils.sendMessage(sender, MessageKind.SUCCESS, "The script has been created");
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.worlds())
            .when("trigger", s -> {
                return ScriptBlockImproved.getInstance().getTriggerRegistry().getTriggers().stream()
                    .filter(t -> t.getName().startsWith(s))
                    .map(Trigger::getName)
                    .collect(Collectors.toList());
            })
            .build(data.getArgName(), data.getCurrentValue());
    }
}
