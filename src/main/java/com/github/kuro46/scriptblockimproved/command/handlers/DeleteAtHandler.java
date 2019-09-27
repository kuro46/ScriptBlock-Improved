package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class DeleteAtHandler extends CommandHandler {

    private final Scripts scripts;

    public DeleteAtHandler(final Scripts scripts) {
        super(Args.builder()
                .required("world")
                .required("x")
                .required("y")
                .required("z")
                .build());

        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
    }

    @Override
    public void execute(
            final CommandManager manager,
            final CommandSender sender,
            final ParsedArgs args) {
        final BlockPosition position = BlockPosition.fromArgs(args).orElse(null);
        if (position == null) {
            return;
        }

        if (scripts.contains(position)) {
            scripts.removeAll(position);
            sendMessage(sender, MessageKind.SUCCESS, "Script(s) has been deleted");
        } else {
            sendMessage(sender, MessageKind.ERROR, "Script not exists");
        }
    }
}
