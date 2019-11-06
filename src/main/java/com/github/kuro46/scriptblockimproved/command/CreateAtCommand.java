package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CandidateBuilder;
import com.github.kuro46.scriptblockimproved.common.command.CandidateFactories;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CompletionData;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.InvalidNumberException;
import com.github.kuro46.scriptblockimproved.script.Script;
import com.github.kuro46.scriptblockimproved.script.ScriptMap;
import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlerMap;
import com.github.kuro46.scriptblockimproved.script.option.OptionList;
import com.github.kuro46.scriptblockimproved.script.option.ParseException;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerRegistry;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CreateAtCommand extends Command {

    @NonNull
    private final OptionHandlerMap handlers;
    @NonNull
    private final ScriptMap scripts;
    @NonNull
    private final TriggerRegistry triggerRegistry;

    public CreateAtCommand() {
        super(
            "createAt",
            Args.builder()
                .requiredArgs("world", "x", "y", "z", "trigger", "options")
                .build()
        );
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        this.handlers = sbi.getOptionHandlers();
        this.scripts = sbi.getScripts();
        this.triggerRegistry = sbi.getTriggerRegistry();
    }

    @Override
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();
        // Parse options
        final OptionList options;
        try {
            final String rawOptions = args.getOrFail("options");
            options = OptionList.parse(handlers, rawOptions);
        } catch (final ParseException e) {
            sendMessage(sender, MessageKind.ERROR, e.getMessage());
            return;
        }
        // Parse position
        final BlockPosition position;
        try {
            position = BlockPosition.fromArgs(args);
        } catch (final InvalidNumberException e) {
            sendMessage(sender, MessageKind.ERROR, e.getMessage());
            return;
        }
        // Get and validate trigger name
        final TriggerName triggerName = TriggerName.of(args.getOrFail("trigger"));
        if (!triggerRegistry.isRegistered(triggerName)) {
            sendMessage(
                    sender,
                    MessageKind.ERROR,
                    "Trigger: '%s' is not registered",
                    triggerName);
            return;
        }
        // Build script
        final Script script = Script.builder()
                    .author(Author.fromCommandSender(sender))
                    .createdAt(System.currentTimeMillis())
                    .triggerName(triggerName)
                    .position(position)
                    .options(options)
                    .build();
        scripts.add(script);
        sendMessage(sender, MessageKind.SUCCESS, "The script has been created");
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.worlds())
            .when("trigger", CandidateFactories.filter(value -> {
                return triggerRegistry.getView().keySet().stream()
                    .map(TriggerName::toString)
                    .collect(Collectors.toList());
            }))
            .build(data.getArgName(), data.getCurrentValue());
    }
}
