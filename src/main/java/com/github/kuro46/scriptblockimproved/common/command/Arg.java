package com.github.kuro46.scriptblockimproved.common.command;

import java.util.Formattable;
import java.util.Formatter;
import lombok.ToString;

/**
 * A representation of an argument.
 * <p>
 * <pre>{@code
 * Arg arg1 = new Arg("argument name", true, null");
 * Arg arg2 = new Arg("argument name", true);
 * assertEquals(arg1, arg2);
 * }</pre>
 */
@ToString
public final class Arg implements Formattable {

    private final ArgName name;
    private final boolean required;

    public Arg(final String name, final boolean required) {
        this.name = ArgName.of(name);
        this.required = required;
    }

    public ArgName getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isOptional() {
        return !required;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Arg)) {
            return false;
        }
        final Arg castedOther = (Arg) other;

        return name.equals(castedOther.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void formatTo(
            final Formatter formatter,
            final int flags,
            final int width,
            final int precision) {
        if (name.toString().isEmpty()) {
            return;
        }
        final char begin = isRequired() ? '<' : '[';
        final char end = isRequired() ? '>' : ']';
        formatter.format("%s%s%s", begin, name, end);
    }
}
