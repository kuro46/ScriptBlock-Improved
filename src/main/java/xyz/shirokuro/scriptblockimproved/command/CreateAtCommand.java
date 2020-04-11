package xyz.shirokuro.scriptblockimproved.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
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

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CreateAtCommand {

    @Executor(command = "sbi createat <world> <x> <y> <z> <trigger> <options>", description = "TODO")
    public void execute(final ExecutionData data) {
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
    public List<String> complete(final CompletionData data) {
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
