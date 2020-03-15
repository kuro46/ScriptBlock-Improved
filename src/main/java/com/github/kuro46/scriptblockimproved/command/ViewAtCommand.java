package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.CandidateFactories;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.commandutility.ParsedArgs;
import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.Script;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ViewAtCommand extends Command {

    public ViewAtCommand() {
        super(
            "viewat",
            Args.builder()
                .required("world", "x", "y", "z")
                .build()
        );
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        final ParsedArgs args = data.getArgs();

        final BlockPosition position = BlockPosition.parseArgs(sender, args).orElse(null);
        if (position == null) {
            return;
        }

        final List<Script> scripts = ScriptBlockImproved.getInstance().getScriptList().get(position);
        if (scripts.isEmpty()) {
            sendMessage(sender, MessageKind.ERROR, "Script not exists");
        } else {
            scripts.forEach(script -> {
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
        sendMessage(sender, "args:");
        script.getOptions().forEach(option -> {
            sendMessage(sender, "  %s: ", option.getName());
            option.getArgs().forEach(value -> sendMessage(sender, "    %s", value));
        });
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.worlds())
            .build(data.getArgName(), data.getCurrentValue());
    }
}
