package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.Script;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ViewAtHandler extends CommandHandler {

    private final Scripts scripts;

    public ViewAtHandler(final Scripts scripts) {
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

        if (!scripts.contains(position)) {
            sendMessage(sender, MessageKind.ERROR, "Script not exists");
        } else {
            scripts.get(position).forEach(script -> {
                sendMessage(sender, "-----");
                showScript(sender, script);
            });
            sendMessage(sender, "-----");
        }
    }

    private void showScript(final CommandSender sender, final Script script) {
        sendMessage(sender,
                "author: %s%s",
                ChatColor.RESET,
                script.getAuthor().getName());
        sendMessage(sender,
                "trigger: %s%s",
                ChatColor.RESET,
                script.getTrigger().getName());
        sendMessage(sender, "options:");
        script.getOptions().forEach(option -> {
            sendMessage(sender, "  %s: ", option.getName().getName());
            option.getArgs().asMap().forEach((key, value) -> {
                sendMessage(sender, "    %s: %s", key, value);
            });
        });
    }
}
