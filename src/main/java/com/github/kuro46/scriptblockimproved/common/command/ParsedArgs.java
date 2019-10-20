package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.ToString;

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
@ToString
public final class ParsedArgs {

    @NonNull
    private final ImmutableMap<ArgName, String> args;

    public ParsedArgs(@NonNull final Map<ArgName, String> args) {
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

    public static class Builder {

        private final ImmutableMap.Builder<ArgName, String> args = ImmutableMap.builder();

        public Builder put(@NonNull final String name, @NonNull final String value) {
            return put(ArgName.of(name), value);
        }

        public Builder put(@NonNull final ArgName name, @NonNull final String value) {
            args.put(name, value);

            return this;
        }

        public ParsedArgs build() {
            return new ParsedArgs(args.build());
        }
    }
}
