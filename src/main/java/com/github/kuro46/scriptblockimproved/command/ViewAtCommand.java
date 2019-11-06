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
import java.util.List;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ViewAtCommand extends Command {

    @NonNull
    private final ScriptMap scripts;

    public ViewAtCommand() {
        super(
            "viewat",
            Args.builder()
                .requiredArgs("world", "x", "y", "z")
                .build()
        );
        this.scripts = ScriptBlockImproved.getInstance().getScripts();
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        final ParsedArgs args = data.getArgs();

        final BlockPosition position;
        try {
            position = BlockPosition.fromArgs(args);
        } catch (final InvalidNumberException e) {
            sendMessage(sender, MessageKind.ERROR, e.getMessage());
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
                script.getTriggerName());
        sendMessage(sender, "options:");
        script.getOptions().forEach(option -> {
            sendMessage(sender, "  %s: ", option.getName());
            option.getArgs().asMap().forEach((key, value) -> {
                sendMessage(sender, "    %s: %s", key, value);
            });
        });
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.worlds())
            .build(data.getArgName(), data.getCurrentValue());
    }
}
