package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.shirokuro.commandutility.CompletionData;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Completer;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.Script;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import xyz.shirokuro.scriptblockimproved.common.StringFormatter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

/**
 * A class for handle {@code "/sbi viewat"} and {@code "/sbi view"}
 */
public final class ViewCommands {

    @Executor(command = "sbi view", description = "TODO")
    public void executeView(final ExecutionData data) {
        final Player player = data.getSenderAsPlayer();
        if (player == null) {
            MessageUtils.sendMessage(data.getSender(), MessageKind.ERROR, "Cannot perform this command from the console");
            return;
        }
        MessageUtils.sendMessage(player, "Click any block to view information about scripts in the block");
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            final String cmd = new StringFormatter("sbi viewat %s %s %s %s")
                .argument(location.getWorld().getName())
                .argument(location.getBlockX())
                .argument(location.getBlockY())
                .argument(location.getBlockZ())
                .build();
            player.performCommand(cmd);
        });
    }

    @Executor(command = "sbi viewat <world> <x> <y> <z>", description = "TODO")
    public void executeViewAt(final ExecutionData data) {
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
    public List<String> completeViewAt(final CompletionData data) {
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
