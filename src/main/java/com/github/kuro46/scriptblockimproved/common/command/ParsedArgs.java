package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A representation of parsed args
 * <p>
 * <pre>{@code
 * Args args = Args.builder()
 *     .required("first")
 *     .optional("second")
 *     .build();
 * ParsedArgs parsed = args.parse(Arrays.asList("foo"));
 *
 * parsed.getOrFail("second"); // => throws IllegalArgumentException
 * parsed.getOrNull("second"); // => null
 * parsed.get("second"); // => Optional.empty()
 *
 * parsed.getOrNull("first"); // => "first"
 * parsed.getOrFail("first"); // => "first"
 * parsed.get("first").get(); // => "first"
 * }</pre>
 */
public final class ParsedArgs {

    private final ImmutableMap<ArgName, String> args;

    public ParsedArgs(final Map<ArgName, String> args) {
        Objects.requireNonNull(args, "'args' cannot be null");

        this.args = ImmutableMap.copyOf(args);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<String> get(final String name) {
        return Optional.ofNullable(args.get(ArgName.of(name)));
    }

    public String getOrNull(final String name) {
        return get(name).orElse(null);
    }

    public String getOrFail(final String name) {
        return get(name).orElseThrow(() -> {
            final String message = String.format("Argument not exists: %s", name);
            return new IllegalArgumentException(message);
        });
    }

    public ImmutableMap<ArgName, String> asMap() {
        return args;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("args", args)
            .toString();
    }

    public static class Builder {

        private final ImmutableMap.Builder<ArgName, String> args = ImmutableMap.builder();

        public Builder put(final String name, final String value) {
            Objects.requireNonNull(name, "'name' cannot be null");
            Objects.requireNonNull(value, "'value' cannot be null");

            return put(ArgName.of(name), value);
        }

        public Builder put(final ArgName name, final String value) {
            Objects.requireNonNull(name, "'name' cannot be null");
            Objects.requireNonNull(value, "'value' cannot be null");

            args.put(name, value);

            return this;
        }

        public ParsedArgs build() {
            return new ParsedArgs(args.build());
        }
    }
}
