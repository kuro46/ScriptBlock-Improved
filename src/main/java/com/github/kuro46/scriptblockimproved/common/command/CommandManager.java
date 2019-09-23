package com.github.kuro46.scriptblockimproved.common.command;

import com.github.kuro46.scriptblockimproved.common.tuple.Pair;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class CommandManager {

    private final Map<CommandSection, Command> commands = new LinkedHashMap<>();
    private final List<ErrorHandler> errorHandlers = new ArrayList<>();
    private final ExecutorImpl executor = new ExecutorImpl();

    public void registerCommand(final Command command) {
        final Command prev = commands.put(command.getSection(), command);

        if (prev == null) {
            Bukkit.getPluginCommand(command.getSection().getName()).setExecutor(executor);
        }
    }

    public ImmutableMap<CommandSection, Command> asMap() {
        return ImmutableMap.copyOf(commands);
    }

    public void addErrorHandler(final ErrorHandler handler) {
        errorHandlers.add(handler);
    }

    private Optional<Pair<Command, Integer>> findCommand(final List<CommandSection> sections) {
        int nest = 0;
        Command command = null;
        for (final CommandSection section : sections) {
            final Command retrieved = command == null
                ? commands.get(section)
                : command.getChild(section).orElse(null);

            if (retrieved == null) {
                break;
            }

            nest++;
            command = retrieved;
        }

        return command == null
            ? Optional.empty()
            : Optional.of(Pair.of(command, nest));
    }

    private void onCommand(
            final CommandSender sender,
            final String rootName,
            final List<String> args) {
        final List<String> stringSections = new ArrayList<String>() {
            {
                add(rootName);
                addAll(args);
            }
        };

        final Pair<Command, Integer> result = findCommand(stringSections.stream()
                .map(CommandSection::new)
                .collect(Collectors.toList())).orElse(null);
        if (result == null) {
            errorHandlers.forEach(handler -> handler.onUnknownCommand(sender, stringSections));
            return;
        }

        final Command command = result.left();
        final int nest = result.right();

        final List<String> trimmedArgs = stringSections.subList(
                nest,
                stringSections.size());

        final ParsedArgs parsedArgs =
            command.getHandler().getArgs().parse(trimmedArgs).orElse(null);
        if (parsedArgs == null) {
            errorHandlers.forEach(handler -> handler.onParseFailed(sender, command));
            return;
        }

        command.getHandler().execute(this, sender, parsedArgs);
    }

    public interface ErrorHandler {

        void onUnknownCommand(CommandSender sender, List<String> sections);

        void onParseFailed(CommandSender sender, Command command);
    }

    private class ExecutorImpl implements CommandExecutor {

        @Override
        public boolean onCommand(
                final CommandSender sender,
                final org.bukkit.command.Command command,
                final String label,
                final String[] args) {
            CommandManager.this.onCommand(sender, command.getName(), Arrays.asList(args));
            return true;
        }
    }
}
