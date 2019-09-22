package com.github.kuro46.scriptblockimproved.common.command;

import com.github.kuro46.scriptblockimproved.common.ListUtils;
import com.github.kuro46.scriptblockimproved.common.tuple.Pair;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Formattable;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class Args implements Formattable {

    private static final Args EMPTY = new Args(ImmutableList.of());

    private final ImmutableList<Arg> args;

    public Args(final List<Arg> args) {
        Objects.requireNonNull(args, "'args' cannot be null");

        Args.validate(args);

        this.args = ImmutableList.copyOf(args);
    }

    public static Args empty() {
        return EMPTY;
    }

    public ImmutableList<Arg> asList() {
        return args;
    }

    private static void validate(final List<Arg> args) {
        // Checks duplication

        final Set<ArgName> names = new HashSet<>();
        for (final Arg arg : args) {
            final ArgName name = arg.getName();
            if (!names.add(name)) {
                final String message = String.format("Duplicated argument: %s", name);
                throw new IllegalArgumentException(message);
            }
        }

        // Validates argument order

        boolean prevIsRequired = true;
        for (final Arg arg : args) {
            final boolean currentIsRequired = arg.isRequired();

            if (currentIsRequired && !prevIsRequired) {
                throw new IllegalArgumentException("Invalid argument order");
            }

            prevIsRequired = arg.isRequired();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<ParsedArgs> parse(final List<String> raw) {
        return new Parser().parse(raw);
    }

    // TODO: access methods

    @Override
    public void formatTo(
            final Formatter formatter,
            final int flags,
            final int width,
            final int precision) {
        final String appended = args.stream()
            .map(arg -> String.format("%s", arg))
            .collect(Collectors.joining(" "));
        formatter.format("%s", appended);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("args", args)
            .toString();
    }

    private class Parser {

        public Optional<ParsedArgs> parse(final List<String> raw) {
            final List<Pair<ArgName, String>> preparsed = preparse(raw).orElse(null);
            if (preparsed == null) {
                return Optional.empty();
            }
            final Map<ArgName, String> squashed = squash(preparsed);

            return Optional.of(new ParsedArgs(squashed));
        }

        private Optional<List<Pair<ArgName, String>>> preparse(final List<String> parts) {
            final List<Pair<ArgName, String>> preparsed = new ArrayList<>();
            int index = 0;
            Arg prevArg = null;
            while (true) {
                if (!ListUtils.isPresent(args, index)
                        && !ListUtils.isPresent(parts, index)) {
                    break;
                }
                final Arg currentArg = ListUtils.get(args, index).orElse(prevArg);

                final Optional<String> wrappedValue = ListUtils.get(parts, index);
                final String value = wrappedValue.orElse(null);
                if (value == null && currentArg.isRequired()) {
                    return Optional.empty();
                }

                if (value != null) {
                    preparsed.add(Pair.of(currentArg.getName(), value));
                }

                // finalize
                index++;
                prevArg = currentArg;
            }

            return Optional.of(preparsed);
        }

        private Map<ArgName, String> squash(final List<Pair<ArgName, String>> preparsed) {
            final Map<ArgName, String> squashed = new HashMap<>();

            for (final Pair<ArgName, String> pair : preparsed) {
                final ArgName name = pair.left();
                final String value = pair.right();
                if (!squashed.containsKey(name)) {
                    squashed.put(name, value);
                } else {
                    final String appended = squashed.get(name) + " " + value;
                    squashed.put(name, appended);
                }
            }

            return squashed;
        }
    }

    public static class Builder {

        private final ImmutableList.Builder<Arg> args = ImmutableList.builder();

        public Builder add(final Arg arg) {
            Objects.requireNonNull(arg, "'arg' cannot be null");

            args.add(arg);
            return this;
        }

        public Builder add(final String name, final boolean required) {
            Objects.requireNonNull(name, "'name' cannot be null");

            return add(new Arg(name, required));
        }

        public Builder required(final String name) {
            Objects.requireNonNull(name, "'name' cannot be null");

            return add(new Arg(name, true));
        }

        public Builder optional(final String name) {
            Objects.requireNonNull(name, "'name' cannot be null");

            return add(new Arg(name, false));
        }

        public Args build() {
            return new Args(args.build());
        }
    }
}
