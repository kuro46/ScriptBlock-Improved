package com.github.kuro46.scriptblockimproved.common.command;

import com.github.kuro46.scriptblockimproved.common.ListUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

@ToString
public abstract class RootCommand extends Command {

    public RootCommand(@NonNull final String name, @NonNull final Args args) {
        this(CommandName.of(name), args);
    }

    public RootCommand(@NonNull final CommandName name, @NonNull final Args args) {
        super(name, args);
        hookBukkit();
    }

    private void hookBukkit() {
        final String commandName = getName().toString();
        final PluginCommand pluginCommand = Bukkit.getPluginCommand(commandName);
        pluginCommand.setExecutor(this::onCommand);
        pluginCommand.setTabCompleter(this::onTabComplete);
    }

    private boolean onCommand(
            @NonNull final CommandSender sender,
            @NonNull final org.bukkit.command.Command bukkitCommand,
            @NonNull final String label,
            @NonNull final String[] args) {
        validateIncomingCommand(bukkitCommand);
        final FindResult result = findCommand(FindMode.FOR_EXECUTION, Arrays.asList(args));
        final ImmutableList<CommandName> path = new ImmutableList.Builder<CommandName>()
            .add(CommandName.of(bukkitCommand.getName()))
            .addAll(result.getUsed().stream()
                .map(CommandName::new)
                .collect(Collectors.toList()))
            .build();
        final Command command = result.getCommand();
        final ParsedArgs parsedArgs =
            command.getArgs().parse(result.getFree()).orElse(null);
        if (parsedArgs == null) {
            onParseFailed(sender, path, command);
            return true;
        }
        final ExecutionData data = ExecutionData.builder()
            .commandPath(path)
            .command(command)
            .args(parsedArgs)
            .dispatcher(sender)
            .root(this)
            .build();
        result.getCommand().execute(data);
        return true;
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
        final FindResult result = findCommand(FindMode.FOR_COMPLETION, args);
        final Command command = result.getCommand();
        final List<String> free = result.getFree();
        // Get completing value and argument name of it
        final List<Arg> argsInfo = command.getArgs().asList();
        if (argsInfo.isEmpty()) return Collections.emptyList();
        final Arg argInfo = ListUtils.get(argsInfo, free.size() - 1)
            .orElse(ListUtils.last(argsInfo).get());
        final String currentValue = ListUtils.last(free)
            .orElseGet(() -> ListUtils.last(result.getUsed()).get());
        // Build data
        final CompletionData data = CompletionData.builder()
            .root(this)
            .dispatcher(sender)
            .command(command)
            .argName(argInfo.getName())
            .currentValue(currentValue)
            .build();
        return command.complete(data);
    }

    // Validates incoming command
    // Throws IllegalStateException if name of incoming command is different from root command
    private void validateIncomingCommand(@NonNull final org.bukkit.command.Command bukkitCommand) {
        final String bukkitCommandName = bukkitCommand.getName();
        final String rootCommandName = getName().toString();
        Preconditions.checkState(bukkitCommandName.equals(rootCommandName));
    }

    private FindResult findCommand(@NonNull FindMode mode, @NonNull final List<String> args) {
        final ImmutableList.Builder<String> used = new ImmutableList.Builder<>();
        int nest = 0;
        Command command = this;
        for (final ListIterator<String> iterator = args.listIterator(); iterator.hasNext();) {
            final String arg = iterator.next();
            if (mode == FindMode.FOR_COMPLETION && !arg.isEmpty() && !iterator.hasNext()) break;
            final CommandName section = CommandName.of(arg);
            final Command retrieved = command.getChild(section).orElse(null);
            if (retrieved == null) break;
            nest++;
            command = retrieved;
            used.add(arg);
        }
        final List<String> free = args.subList(nest, args.size());
        return new FindResult(used.build(), command, ImmutableList.copyOf(free));
    }


    public abstract void onParseFailed(
        final CommandSender sender,
        final ImmutableList<CommandName> commandPath,
        final Command command
    );

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

    private enum FindMode {
        FOR_COMPLETION,
        FOR_EXECUTION
    }
}
