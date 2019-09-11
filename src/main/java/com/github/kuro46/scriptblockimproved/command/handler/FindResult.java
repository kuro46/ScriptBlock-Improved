package com.github.kuro46.scriptblockimproved.command.handler;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;

public final class FindResult {

    private final Command command;
    private final ImmutableList<String> args;

    public FindResult(final Command command, final List<String> args) {
        this.command = Objects.requireNonNull(command, "'command' cannot be null");
        this.args = ImmutableList.copyOf(Objects.requireNonNull(args, "'args' cannot be null"));
    }

    public Command getCommand() {
        return command;
    }

    public ImmutableList<String> getArgs() {
        return args;
    }
}
