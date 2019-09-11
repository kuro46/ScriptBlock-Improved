package com.github.kuro46.scriptblockimproved.command.syntax;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Argument {

    /**
     * Matches with {@code "<any string>"} or {@code "[any string]"}
     */
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("[<\\[](.*?)[\\]>]");

    private final String name;
    private final boolean required;

    public Argument(final String name, final boolean required) {
        Objects.requireNonNull(name, "'name' cannot be null");

        this.name = name;
        this.required = required;
    }

    /**
     * <pre>
     * {@code
     * assertEqual(new Argument("aaa", false), "[aaa]");
     * assertEqual(new Argument("bbb", true), "<bbb>");
     * }
     * </pre>
     */
    public static Optional<Argument> fromString(final String str) {
        final boolean required = str.startsWith("<");
        final Matcher matcher = ARGUMENT_PATTERN.matcher(str);

        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.of(new Argument(matcher.group(1), required));
    }

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return !required;
    }

    public boolean isRequired() {
        return required;
    }
}
