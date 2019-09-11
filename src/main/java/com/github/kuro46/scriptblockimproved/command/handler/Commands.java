package com.github.kuro46.scriptblockimproved.command.handler;

import com.github.kuro46.commandutility.StringConverters;
import com.github.kuro46.scriptblockimproved.command.syntax.Argument;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class Commands implements CommandExecutor {

    private final Map<CommandSections, Command> commands = new HashMap<>();
    private final List<CommandsListener> listeners = new ArrayList<>();
    private final StringConverters converters;

    public Commands(final StringConverters converters) {
        this.converters = Objects.requireNonNull(converters, "'converters' cannot be null");
    }

    public Optional<FindResult> findCommandFor(final List<String> strings) {
        CommandSections currentSections = new CommandSections(Collections.emptyList());
        Command command = null;
        for (final String string : strings) {
            currentSections = currentSections.child(string);

            final Command got = commands.get(currentSections);
            if (got != null) {
                command = got;
            }
        }
        if (command == null) {
            return Optional.empty();
        }

        final List<String> args = strings.subList(
                command.getSections().getView().size(),
                strings.size());

        return Optional.of(new FindResult(command, args));
    }

    public void addListener(final CommandsListener listener) {
        listeners.add(listener);
    }

    private AssertResult assertSyntax(final List<Argument> syntax, final List<String> args) {
        final int lastIndex = args.size() - 1;
        for (int i = 0; i < syntax.size(); i++) {
            final Argument s = syntax.get(i);
            if (s.isRequired() && lastIndex < i) {
                return AssertResult.FAILURE;
            }
        }
        return AssertResult.SUCCESS;
    }

    public void register(final Command command) {
        commands.put(command.getSections(), command);
    }

    @Override
    public boolean onCommand(
            final CommandSender sender,
            final org.bukkit.command.Command bukkitCommand,
            final String label,
            final String[] bukkitArgs) {
        final FindResult result = findCommandFor(new ImmutableList.Builder<String>()
                .add(bukkitCommand.getName())
                .addAll(Arrays.asList(bukkitArgs))
                .build())
            .orElseGet(() -> {
                listeners.forEach(listener -> {
                    listener.onUnknownCommand(sender);
                });
                return null;
            });
        if (result == null) return true;

        final Command command = result.getCommand();
        final CommandHandler handler = command.getHandler();
        final ImmutableList<String> args = result.getArgs();

        if (assertSyntax(handler.getSyntax().getView(), args) == AssertResult.FAILURE) {
            listeners.forEach(listener -> {
                listener.onInvalidSyntax(sender);
            });
            return true;
        }

        handler.execute(sender, new ExecutionArguments(converters, args));
        return true;
    }
}
