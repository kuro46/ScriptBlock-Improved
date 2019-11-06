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
     * /foo bar buz &lt;arg1&gt; &lt;arg2&gt;...
     *  \--here---/
     * </pre>
     */
    @Getter
    @NonNull
    private final ImmutableList<CommandName> commandPath;

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
     * /foo bar buz &lt;arg1&gt; &lt;arg2&gt;...
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
    private final RootCommand root;

    public ExecutionData(
            @NonNull final List<CommandName> commandPath,
            @NonNull final Command command,
            @NonNull final ParsedArgs args,
            @NonNull final CommandSender dispatcher,
            @NonNull final RootCommand root) {
        this.commandPath = ImmutableList.copyOf(commandPath);
        this.command = command;
        this.args = args;
        this.dispatcher = dispatcher;
        this.root = root;
    }
}
