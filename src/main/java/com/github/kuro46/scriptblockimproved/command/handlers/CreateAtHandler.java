package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.Script;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CreateAtHandler extends CommandHandler {

    private final OptionHandlers handlers;
    private final Scripts scripts;

    public CreateAtHandler(final OptionHandlers handlers, final Scripts scripts) {
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
    public void execute(
            final CommandManager manager,
            final CommandSender sender,
            final ParsedArgs args) {
        final String trigger = args.getOrFail("trigger");
        final String rawOptions = args.getOrFail("script");
        final Options options = Options.parse(handlers, rawOptions)
            .orElseThrow(IllegalArgumentException::new);
        final BlockPosition position = BlockPosition.fromArgs(args).orElse(null);
        if (position == null) {
            return;
        }
        final Author author = Author.fromCommandSender(sender);

        if (scripts.contains(position)) {
            sendMessage(sender,
                    MessageKind.ERROR,
                    "Script already exists at that place. Instead use '/sbi add[at]'");
            return;
        }

        scripts.add(new Script(
                    System.currentTimeMillis(),
                    TriggerName.of(trigger),
                    author,
                    position,
                    options));
        sendMessage(sender, MessageKind.SUCCESS, "The script has been created");
    }
}
