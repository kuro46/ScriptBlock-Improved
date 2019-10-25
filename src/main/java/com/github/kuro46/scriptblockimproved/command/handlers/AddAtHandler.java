package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CandidateBuilder;
import com.github.kuro46.scriptblockimproved.common.command.CandidateFactories;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CompletionData;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
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

public final class AddAtHandler extends CommandHandler {

    @NonNull
    private final OptionHandlerMap handlers;
    @NonNull
    private final ScriptMap scripts;
    @NonNull
    private final TriggerRegistry triggerRegistry;

    public AddAtHandler() {
        super(Args.builder()
                .required("world")
                .required("x")
                .required("y")
                .required("z")
                .required("trigger")
                .required("script")
                .build());
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        this.handlers = sbi.getOptionHandlers();
        this.scripts = sbi.getScripts();
        this.triggerRegistry = sbi.getTriggerRegistry();
    }

    @Override
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();

        final String rawOptions = args.getOrFail("script");
        final OptionList options;
        try {
            options = OptionList.parse(handlers, rawOptions);
        } catch (final ParseException e) {
            sendMessage(sender, MessageKind.ERROR, e.getMessage());
            return;
        }
        final BlockPosition position = BlockPosition.fromArgs(args).orElse(null);
        if (position == null) {
            return;
        }
        final Author author = Author.fromCommandSender(sender);

        if (!scripts.contains(position)) {
            sendMessage(sender,
                    MessageKind.ERROR,
                    "Script not exists at that place. Instead use '/sbi create[at]'");
            return;
        }

        final TriggerName triggerName = TriggerName.of(args.getOrFail("trigger"));
        if (!triggerRegistry.isRegistered(triggerName)) {
            sendMessage(
                    sender,
                    MessageKind.ERROR,
                    "Trigger: '%s' is not registered",
                    triggerName);
            return;
        }
        scripts.add(new Script(
                    System.currentTimeMillis(),
                    triggerName,
                    author,
                    position,
                    options));
        sendMessage(
                sender,
                MessageKind.ERROR,
                "The script has been added");
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
