package com.github.kuro46.scriptblockimproved.command.handlers;

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
import com.github.kuro46.scriptblockimproved.script.Scripts;
import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class AddAtHandler extends CommandHandler {

    private final OptionHandlers handlers;
    private final Scripts scripts;

    public AddAtHandler(final OptionHandlers handlers, final Scripts scripts) {
        super(Args.builder()
                .required("world")
                .required("x")
                .required("y")
                .required("z")
                .required("trigger")
                .required("script")
                .build());

        this.handlers = Objects.requireNonNull(handlers, "'handlers' cannot be null");
        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
    }

    @Override
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();

        final String trigger = args.getOrFail("trigger");
        final String rawOptions = args.getOrFail("script");
        final Options options = Options.parse(handlers, rawOptions)
            .orElseThrow(IllegalArgumentException::new);
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

        scripts.add(new Script(
                    System.currentTimeMillis(),
                    TriggerName.of(trigger),
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
            .build(data.getArgName(), data.getCurrentValue());
    }
}
