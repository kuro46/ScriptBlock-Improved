package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CandidateBuilder;
import com.github.kuro46.scriptblockimproved.common.command.CandidateFactories;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CompletionData;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ListHandler extends CommandHandler {

    @NonNull
    private final Scripts scripts;

    public ListHandler() {
        super(Args.builder()
                .optional("world")
                .build());
        this.scripts = ScriptBlockImproved.getInstance().getScripts();
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        final String world = data.getArgs().getOrNull("world");
        final List<BlockPosition> positions = scripts.getPositions().stream()
            .filter(position -> world == null || world.equals(position.getWorld()))
            .sorted()
            .collect(Collectors.toList());
        if (positions.isEmpty()) {
            sendMessage(sender, MessageKind.ERROR, "Empty");
            return;
        }
        int count = 0;
        for (final BlockPosition position : positions) {
            sendMessage(sender,
                    "[%s] %s/%s/%s/%s",
                    ++count,
                    position.getWorld(),
                    position.getX(),
                    position.getY(),
                    position.getZ());
        }
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.filter(value -> {
                return scripts.getPositions().stream()
                    .map(BlockPosition::getWorld)
                    .distinct()
                    .collect(Collectors.toList());
            }))
            .build(data.getArgName(), data.getCurrentValue());
    }
}
