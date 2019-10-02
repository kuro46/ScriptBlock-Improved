package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.command.CommandSender;

@ToString
@Builder(builderClassName = "Builder")
public final class ExecutionData {

    /**
     * Name of command<br>
     *
     * <pre>
     * /foo bar buz <arg1> <arg2>...
     *  \--here---/
     * </pre>
     */
    @Getter
    @NonNull
    private final ImmutableList<CommandSection> commandPath;

    /**
     * Command to executed
     */
    @Getter
    @NonNull
    private final Command command;

    /**
     * Data of arguments<br>
     *
     * <pre>
     * /foo bar buz <arg1> <arg2>...
     *              \-----here-----/
     * </pre>
     */
    @Getter
    @NonNull
    private final ParsedArgs args;

    /**
     * Dispatcher of command
     */
    @Getter
    @NonNull
    private final CommandSender dispatcher;

    /**
     * Caller of this command
     */
    @Getter
    @NonNull
    private final CommandRoot root;

    public ExecutionData(
            @NonNull final List<CommandSection> commandPath,
            @NonNull final Command command,
            @NonNull final ParsedArgs args,
            @NonNull final CommandSender dispatcher,
            @NonNull final CommandRoot root) {
        this.commandPath = ImmutableList.copyOf(commandPath);
        this.command = command;
        this.args = args;
        this.dispatcher = dispatcher;
        this.root = root;
    }
}
