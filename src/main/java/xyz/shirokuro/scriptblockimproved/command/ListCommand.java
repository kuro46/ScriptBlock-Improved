package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.command.CommandSender;
import xyz.shirokuro.commandutility.CompletionData;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Completer;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ListCommand {

    @Executor(command = "sbi list [world]", description = "TODO")
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getSender();
        final String world = data.get("world");
        final List<BlockPosition> positions = ScriptBlockImproved.getInstance().getScriptList().asUnmodifiableMap().keySet().stream()
            .filter(position -> world == null || world.equalsIgnoreCase(position.getWorld()))
            .sorted()
            .collect(Collectors.toList());
        if (positions.isEmpty()) {
            MessageUtils.sendMessage(sender, MessageKind.ERROR, "Empty");
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

    @Completer(command = "sbi list [world]")
    public List<String> complete(final CompletionData data) {
        if (data.getName().equals("world")) {
            return ScriptBlockImproved.getInstance().getScriptList().asUnmodifiableMap().keySet().stream()
                .map(BlockPosition::getWorld)
                .distinct()
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
