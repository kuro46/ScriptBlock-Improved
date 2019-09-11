package com.github.kuro46.scriptblockimproved.command.handler;

import com.github.kuro46.commandutility.StringConverters;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;

public final class ExecutionArguments {

    private final StringConverters converters;
    private final ImmutableList<String> args;

    public ExecutionArguments(final StringConverters converters, final List<String> args) {
        this.converters = Objects.requireNonNull(converters, "'converters' cannot be null");
        this.args = ImmutableList.copyOf(Objects.requireNonNull(args, "'args' cannot be null"));
    }

    public String get(final int index) {
        return args.get(index);
    }

    public <T> Optional<T> get(final Class<T> clazz, final CommandSender sender, final int index) {
        Objects.requireNonNull(clazz, "'clazz' cannot be null");
        Objects.requireNonNull(sender, "'sender' cannot be null");

        return Optional.ofNullable(converters.convert(clazz, sender, args.get(index)));
    }

    public ImmutableList<String> getArgs() {
        return args;
    }

    public ExecutionArguments subArgs(int fromIndex) {
        return new ExecutionArguments(converters, args.subList(fromIndex, args.size()));
    }

    public Stream<String> stream() {
        return args.stream();
    }
}
