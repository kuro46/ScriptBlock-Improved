package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.commandutility.CompletionData;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Completer;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.ScriptList;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RemoveAtCommand {

    @Executor(command = "sbi removeat <world> <x> <y> <z>", description = "TODO")
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getSender();
        final Map<String, String> args = data.getArgs();
        final BlockPosition position = BlockPosition.parseArgs(sender, args).orElse(null);
        if (position == null) {
            return;
        }
        final ScriptList scriptList = ScriptBlockImproved.getInstance().getScriptList();
        if (!scriptList.get(position).isEmpty()) {
            scriptList.removeAll(position);
            MessageUtils.sendMessage(sender, MessageKind.SUCCESS, "Script(s) has been removed");
        } else {
            MessageUtils.sendMessage(sender, MessageKind.ERROR, "Script not exists");
        }
    }

    @Completer(command = "sbi removeat <world> <x> <y> <z>")
    public List<String> complete(final CompletionData data) {
        if (data.getName().equals("world")) {
            return Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(s -> s.startsWith(data.getCurrentValue()))
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
