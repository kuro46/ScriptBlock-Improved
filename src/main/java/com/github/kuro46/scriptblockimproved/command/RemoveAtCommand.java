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
import com.github.kuro46.scriptblockimproved.script.ScriptMap;
import java.util.List;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class RemoveAtCommand extends Command {

    @NonNull
    private final ScriptMap scripts;

    public RemoveAtCommand() {
        super(
            "removeat",
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

        if (scripts.contains(position)) {
            scripts.removeAll(position);
            sendMessage(sender, MessageKind.SUCCESS, "Script(s) has been removed");
        } else {
            sendMessage(sender, MessageKind.ERROR, "Script not exists");
        }
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.worlds())
            .build(data.getArgName(), data.getCurrentValue());
    }
}
