package com.github.kuro46.scriptblockimproved.common.command;

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

    private final Map<CommandName, Command> commands = new LinkedHashMap<>();
    private final List<ErrorHandler> errorHandlers = new ArrayList<>();
    private final ExecutorImpl executor = new ExecutorImpl();

    public void registerCommand(final Command command) {
        final Command prev = commands.put(command.getName(), command);

        if (prev == null) {
            final String firstSecName = command.getName().asSections().get(0).getName();
            Bukkit.getPluginCommand(firstSecName).setExecutor(executor);
        }
    }

    public ImmutableMap<CommandName, Command> asMap() {
        return ImmutableMap.copyOf(commands);
    }

    public void addErrorHandler(final ErrorHandler handler) {
        errorHandlers.add(handler);
    }

    private Optional<Command> findCommand(final List<CommandSection> sections) {
        final List<CommandSection> building = new ArrayList<>();

        Command command = null;
        for (final CommandSection section : sections) {
            building.add(section);

            final Command retrieved = commands.get(CommandName.fromSections(building));
            if (retrieved != null) {
                command = retrieved;
            }
        }

        return Optional.ofNullable(command);
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

        final Command command = findCommand(stringSections.stream()
                .map(CommandSection::new)
                .collect(Collectors.toList())).orElse(null);
        if (command == null) {
            errorHandlers.forEach(handler -> handler.onUnknownCommand(sender, stringSections));
            return;
        }

        final List<String> trimmedArgs = stringSections.subList(
                command.getName().asSections().size(),
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
