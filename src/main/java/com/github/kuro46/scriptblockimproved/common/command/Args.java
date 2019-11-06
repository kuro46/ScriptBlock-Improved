package com.github.kuro46.scriptblockimproved.common.command;

import com.github.kuro46.scriptblockimproved.common.ListUtils;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formattable;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * List of arguments.
 * <p>
 * <pre>{@code
 * Args args = Args.builder()
 *     .required("reqArg1")
 *     .required("reqArg2")
 *     .optional("optArg1")
 *     .build();
 * assertEquals(String.format("%s", args), "<reqArg1> <reqArg2> [optArg1]");
 *
 * Optional<ParsedArgs> parsed = args.parse(Arrays.asList("foo", "bar", "2000"));
 * assertTrue(parsed.isPresent());
 *
 * Optional<ParsedArgs> parsed = args.parse(Arrays.asList("foo", "bar"));
 * assertTrue(parsed.isPresent());
 *
 * Optional<ParsedArgs> parsed = args.parse(Arrays.asList("foo"));
 * assertFalse(parsed.isPresent());
 * }</pre>
 */
@ToString
public final class Args implements Formattable {

    private static final Args EMPTY = new Args(ImmutableList.of());

    private final ImmutableList<Arg> args;

    public Args(@NonNull final List<Arg> args) {
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

    private class Parser {

        public Optional<ParsedArgs> parse(final List<String> raw) {
            if (args.isEmpty()) return Optional.of(new ParsedArgs(Collections.emptyMap()));
            final List<ImmutablePair<ArgName, String>> preparsed = preparse(raw).orElse(null);
            if (preparsed == null) {
                return Optional.empty();
            }
            final Map<ArgName, String> squashed = squash(preparsed);

            return Optional.of(new ParsedArgs(squashed));
        }

        private Optional<List<ImmutablePair<ArgName, String>>> preparse(final List<String> parts) {
            final List<ImmutablePair<ArgName, String>> preparsed = new ArrayList<>();
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
                    preparsed.add(ImmutablePair.of(currentArg.getName(), value));
                }

                // finalize
                index++;
                prevArg = currentArg;
            }

            return Optional.of(preparsed);
        }

        private Map<ArgName, String> squash(final List<ImmutablePair<ArgName, String>> preparsed) {
            final Map<ArgName, String> squashed = new HashMap<>();

            for (final ImmutablePair<ArgName, String> pair : preparsed) {
                final ArgName name = pair.getLeft();
                final String value = pair.getRight();
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

        public Builder add(@NonNull final Arg arg) {
            args.add(arg);
            return this;
        }

        public Builder add(@NonNull final String name, final boolean required) {
            return add(new Arg(name, required));
        }

        public Builder required(@NonNull final String name) {
            return add(new Arg(name, true));
        }

        public Builder optional(@NonNull final String name) {
            return add(new Arg(name, false));
        }

        public Builder optionalArgs(@NonNull final String... names) {
            for (String name : names) optional(name);
            return this;
        }

        public Builder requiredArgs(@NonNull final String... names) {
            for (String name : names) required(name);
            return this;
        }

        public Args build() {
            return new Args(args.build());
        }
    }
}
