package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.shirokuro.commandutility.CompletionData;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Completer;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.OptionListParser;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.Trigger;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CreateCommand {

    @Executor(command = "sbi create <trigger> <options>", description = "TODO")
    public void execute(final ExecutionData data) {
        final Player player = data.getSenderAsPlayer();
        if (player == null) {
            MessageUtils.sendMessage(data.getSender(),
                MessageKind.ERROR,
                "Cannot perform this command from the console");
            return;
        }
        MessageUtils.sendMessage(player, "Click any block to create script to the block");
        try {
            OptionListParser.parse(data.get("options"));
        } catch (OptionListParser.ParseException e) {
            MessageUtils.sendMessage(player, ChatColor.RED + "Incorrect script!: " + e.getMessage());
            return;
        }
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            final BlockPosition position = BlockPosition.ofLocation(location);
            player.performCommand(String.format("sbi createat %s %s %s %s %s %s",
                position.getWorld(),
                position.getX(),
                position.getY(),
                position.getZ(),
                data.get("trigger"),
                data.get("options")));
        });
    }

    @Completer(command = "sbi create <trigger> <options>")
    public List<String> complete(final CompletionData data) {
        if (data.getName().equals("trigger")) {
            return ScriptBlockImproved.getInstance().getTriggerRegistry().getTriggers().stream()
                .map(Trigger::getName)
                .filter(s -> s.startsWith(data.getCurrentValue()))
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
