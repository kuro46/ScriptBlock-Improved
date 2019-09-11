package com.github.kuro46.scriptblockimproved.command.syntax;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Arguments {

    private static final Pattern PATTERN = Pattern.compile("[<\\[].*?[]>]");

    private final ImmutableList<Argument> arguments;

    public Arguments(final List<Argument> arguments) {
        Objects.requireNonNull(arguments, "'arguments' cannot be null");

        this.arguments = ImmutableList.copyOf(arguments);
    }

    public static Optional<Arguments> fromString(final String str) {
        Objects.requireNonNull(str, "'str' cannot be null");

        if (str.isEmpty()) {
            return Optional.of(new Arguments(Collections.emptyList()));
        }

        final Matcher matcher = PATTERN.matcher(str);
        if (!matcher.find()) {
            return Optional.empty();
        }

        final List<Argument> arguments = new ArrayList<>();
        Argument lastAdded = null;
        do {
            final Optional<Argument> optArg = Argument.fromString(matcher.group(0));
            if (!optArg.isPresent()) {
                return Optional.empty();
            }
            final Argument arg = optArg.get();
            if (lastAdded != null && lastAdded.isOptional() && arg.isRequired()) {
                return Optional.empty();
            }
            arguments.add(arg);
            lastAdded = arg;
        } while (matcher.find());

        return Optional.of(new Arguments(arguments));
    }

    public ImmutableList<Argument> getView() {
        return arguments;
    }
}
