package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.commandutility.CompletionData;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Completer;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.Script;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ViewAtCommand {

    @Executor(command = "sbi viewat <world> <x> <y> <z>", description = "TODO")
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getSender();
        final BlockPosition position = BlockPosition.parseArgs(sender, data.getArgs()).orElse(null);
        if (position == null) {
            return;
        }

        final List<Script> scripts = ScriptBlockImproved.getInstance().getScriptList().get(position);
        if (scripts.isEmpty()) {
            MessageUtils.sendMessage(sender, MessageKind.ERROR, "Script not exists");
        } else {
            scripts.forEach(script -> {
                MessageUtils.sendMessage(sender, "-----");
                showScript(sender, script);
            });
            MessageUtils.sendMessage(sender, "-----");
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
        MessageUtils.sendMessage(sender, "args:");
        script.getOptions().forEach(option -> {
            final String args = String.join(" ", option.getArgs());
            sendMessage(sender, "  @%s %s", option.getName(), args);
        });
    }

    @Completer(command = "sbi viewat <world> <x> <y> <z>")
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
