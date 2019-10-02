package com.github.kuro46.scriptblockimproved.common.command;

import com.github.kuro46.scriptblockimproved.common.ListUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

@ToString
public final class CommandRoot {

    private final List<Listener> listeners = new ArrayList<>();
    @NonNull
    @Getter
    private final Command rootCommand;

    private CommandRoot(@NonNull Command rootCommand) {
        this.rootCommand = rootCommand;
        hookBukkit();
    }

    public static CommandRoot register(@NonNull final Command rootCommand) {
        return new CommandRoot(rootCommand);
    }

    private void hookBukkit() {
        final String commandName = rootCommand.getSection().getName();
        final PluginCommand pluginCommand = Bukkit.getPluginCommand(commandName);
        pluginCommand.setExecutor(this::onCommand);
        pluginCommand.setTabCompleter(this::onTabComplete);
    }

    public void addListener(@NonNull final Listener listener) {
        listeners.add(listener);
    }

    private boolean onCommand(
            @NonNull final CommandSender sender,
            @NonNull final org.bukkit.command.Command bukkitCommand,
            @NonNull final String label,
            @NonNull final String[] args) {
        validateIncomingCommand(bukkitCommand);
        final FindResult result = findCommand(Arrays.asList(args));
        final ImmutableList<CommandSection> path = new ImmutableList.Builder<CommandSection>()
            .add(CommandSection.of(bukkitCommand.getName()))
            .addAll(result.getUsed().stream()
                .map(CommandSection::new)
                .collect(Collectors.toList()))
            .build();
        final Command command = result.getCommand();
        final ParsedArgs parsedArgs =
            command.getHandler().getArgs().parse(result.getFree()).orElse(null);
        if (parsedArgs == null) {
            fireParseFailed(sender, path, command);
            return true;
        }
        final ExecutionData data = ExecutionData.builder()
            .commandPath(path)
            .command(command)
            .args(parsedArgs)
            .dispatcher(sender)
            .root(this)
            .build();
        result.getCommand().getHandler().execute(data);
        return true;
    }

    private void fireParseFailed(
            @NonNull final CommandSender sender,
            @NonNull final ImmutableList<CommandSection> path,
            @NonNull final Command command) {
        listeners.forEach(listener -> {
            listener.onParseFailed(sender, path, command);
        });
    }

    private List<String> onTabComplete(
            @NonNull final CommandSender sender,
            @NonNull final org.bukkit.command.Command bukkitCommand,
            @NonNull final String alias,
            @NonNull final String[] rawArgs) {
        validateIncomingCommand(bukkitCommand);
        // Remove " " from a raw argument and collect
        final List<String> args = Arrays.stream(rawArgs)
            .filter(arg -> !arg.equals(" "))
            .collect(Collectors.toList());
        // Find command
        final FindResult result = findCommand(args);
        final Command command = result.getCommand();
        final List<String> free = result.getFree();
        // Get completing value and argument name of it
        final List<Arg> argsInfo = command.getHandler().getArgs().asList();
        if (argsInfo.isEmpty()) return Collections.emptyList();
        final Arg argInfo = ListUtils.get(argsInfo, free.size() - 1)
            .orElse(argsInfo.get(argsInfo.size() - 1));
        // Build data
        final CompletionData data = CompletionData.builder()
            .root(this)
            .dispatcher(sender)
            .command(command)
            .argName(argInfo.getName())
            .currentValue(free.get(free.size() - 1))
            .build();
        return command.getHandler().complete(data);
    }

    // Validates incoming command
    // Throws IllegalStateException if name of incoming command is different from root command
    private void validateIncomingCommand(@NonNull final org.bukkit.command.Command bukkitCommand) {
        final String bukkitCommandName = bukkitCommand.getName();
        final String rootCommandName = rootCommand.getSection().getName();
        Preconditions.checkState(bukkitCommandName.equals(rootCommandName));
    }

    private FindResult findCommand(@NonNull final List<String> args) {
        final ImmutableList.Builder<String> used = new ImmutableList.Builder<>();
        int nest = 0;
        Command command = rootCommand;
        for (final String arg : args) {
            final CommandSection section = CommandSection.of(arg);
            final Command retrieved = command.getChild(section).orElse(null);
            if (retrieved == null) break;
            nest++;
            command = retrieved;
            used.add(arg);
        }
        final List<String> free = args.subList(nest, args.size());
        return new FindResult(used.build(), command, ImmutableList.copyOf(free));
    }

    public interface Listener {

        void onParseFailed(
                final CommandSender sender,
                final ImmutableList<CommandSection> commandPath,
                final Command command);
    }

    @AllArgsConstructor
    @ToString
    private static final class FindResult {

        @NonNull
        @Getter
        private final ImmutableList<String> used;
        @NonNull
        @Getter
        private final Command command;
        @NonNull
        @Getter
        private final ImmutableList<String> free;
    }
}
