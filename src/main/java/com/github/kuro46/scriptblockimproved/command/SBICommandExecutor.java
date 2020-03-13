package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.scriptblockimproved.ActionQueue;
import com.github.kuro46.scriptblockimproved.Author;
import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.OptionListParser;
import com.github.kuro46.scriptblockimproved.Script;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.ScriptList;
import com.google.common.collect.ImmutableList;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SBICommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        final ScriptList scriptList = ScriptBlockImproved.getInstance().getScriptList();
        switch (args[0].toLowerCase()) {
            case "list":
                scriptList.forEach((position, script) -> {
                    sender.sendMessage(String.format("W:%s X:%s Y:%s Z:%s %s", position.getWorld(), position.getX(), position.getY(), position.getZ(), script.getOptions()));
                });
                return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Cannot perform this command from the console!");
            return true;
        }
        final ActionQueue actionQueue = ScriptBlockImproved.getInstance().getActionQueue();
        final Player player = (Player) sender;
        switch (args[0].toLowerCase()) {
            case "create":
                sender.sendMessage("Please click any places to create!");
                if (args.length < 3) {
                    sender.sendMessage("Arguments not enough!");
                    return false;
                }
                final String triggerName = args[1];
                final List<Script.Option> options;
                try {
                    options = OptionListParser.parse(Arrays.stream(args).skip(2).collect(Collectors.joining(" ")));
                } catch (OptionListParser.ParseException e) {
                    player.sendMessage(e.getMessage());
                    return true;
                }
                final Script script = Script.builder()
                    .author(Author.player(player))
                    .createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
                    .options(ImmutableList.copyOf(options))
                    .triggerName(triggerName)
                    .build();
                actionQueue.queue(player, location -> {
                    final BlockPosition position = BlockPosition.ofLocation(location);
                    scriptList.add(position, script);
                    player.sendMessage("Created!");
                });
                return true;
            case "remove":
                sender.sendMessage("Please click any places to remove!");
                actionQueue.queue(player, location -> {
                    final BlockPosition position = BlockPosition.ofLocation(location);
                    scriptList.remove(position);
                    player.sendMessage("Removed!");
                });
                return true;
        }
        return true;
    }
}
