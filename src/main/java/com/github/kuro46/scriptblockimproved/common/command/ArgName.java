package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.base.MoreObjects;
import java.util.Formattable;
import java.util.Formatter;
import java.util.Objects;

/**
 * Name of an argument.
 * <p>
 * <pre>{@code
 * ArgName name1 = ArgName.of("Argument Name");
 * ArgName name2 = ArgName.of("argument name");
 * assertEquals(name1, name2);
 * }</pre>
 */
public final class ArgName implements Formattable {

    private final String name;

    private ArgName(final String name) {
        this.name = Objects.requireNonNull(name, "'name' cannot be null").toLowerCase();
    }

    public static ArgName of(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        return new ArgName(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public void formatTo(
            final Formatter formatter,
            final int flags,
            final int width,
            final int precision) {
        formatter.format("%s", name);
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof ArgName)) {
            return false;
        }
        final ArgName castedOther = (ArgName) other;

        return name.equals(castedOther.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .toString();
    }
}
