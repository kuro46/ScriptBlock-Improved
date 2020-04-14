package xyz.shirokuro.scriptblockimproved.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.shirokuro.commandutility.CompletionData;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Completer;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.*;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import xyz.shirokuro.scriptblockimproved.common.StringFormatter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

/**
 * A class for handle {@code "/sbi createat"} and {@code "/sbi create"}
 */
public final class CreateCommands {

    @Executor(command = "sbi create <trigger> <options>", description = "TODO")
    public void executeCreate(final ExecutionData data) {
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
            final String cmd = new StringFormatter("sbi createat %s %s %s %s %s %s")
                .argument(position.getWorld())
                .argument(position.getX())
                .argument(position.getY())
                .argument(position.getZ())
                .argument(data.get("trigger"))
                .argument(data.get("options"))
                .build();
            player.performCommand(cmd);
        });
    }

    @Completer(command = "sbi create <trigger> <options>")
    public List<String> completeCreate(final CompletionData data) {
        if (data.getName().equals("trigger")) {
            return ScriptBlockImproved.getInstance().getTriggerRegistry().getTriggers().stream()
                .map(Trigger::getName)
                .filter(s -> s.startsWith(data.getCurrentValue()))
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Executor(command = "sbi createat <world> <x> <y> <z> <trigger> <options>", description = "TODO")
    public void executeCreateAt(final ExecutionData data) {
        final CommandSender sender = data.getSender();
        // Parse options
        final List<Script.Option> options;
        try {
            final String rawOptions = data.get("options");
            options = OptionListParser.parse(rawOptions);
        } catch (final OptionListParser.ParseException e) {
            sendMessage(sender, MessageKind.ERROR, e.getMessage());
            return;
        }
        // Parse position
        final BlockPosition position = BlockPosition.parseArgs(sender, data.getArgs()).orElse(null);
        if (position == null) {
            return;
        }
        final String trigger = data.get("trigger");
        final Author author;
        if (sender instanceof Player) {
            author = Author.player((Player) sender);
        } else {
            author = Author.system("console");
        }
        // Build script
        final Script script = Script.builder()
            .author(author)
            .createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
            .triggerName(trigger)
            .options(ImmutableList.copyOf(options))
            .build();
        ScriptBlockImproved.getInstance().getScriptList().add(position, script);
        MessageUtils.sendMessage(sender, MessageKind.SUCCESS, "The script has been created");
    }

    @Completer(command = "sbi createat <world> <x> <y> <z> <trigger> <options>")
    public List<String> completeCreateAt(final CompletionData data) {
        final String name = data.getName();
        final String currentValue = data.getCurrentValue();
        switch (name) {
            case "world":
                return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(s -> s.startsWith(currentValue))
                    .collect(Collectors.toList());
            case "trigger":
                return ScriptBlockImproved.getInstance().getTriggerRegistry().getTriggers().stream()
                    .filter(t -> t.getName().startsWith(currentValue))
                    .map(Trigger::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
