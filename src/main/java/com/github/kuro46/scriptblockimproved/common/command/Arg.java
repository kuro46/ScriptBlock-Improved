package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.base.MoreObjects;
import java.util.Formattable;
import java.util.Formatter;
import java.util.Objects;

public final class Arg implements Formattable {

    private final ArgName name;
    private final boolean required;
    private final String description;

    public Arg(final String name, final boolean required) {
        this(name, required, null);
    }

    public Arg(final String name, final boolean required, final String description) {
        Objects.requireNonNull(name, "'name' cannot be null");
        this.name = ArgName.of(name);
        this.required = required;
        this.description = description;
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
    public void formatTo(
            final Formatter formatter,
            final int flags,
            final int width,
            final int precision) {
        if (name.getName().isEmpty()) {
            return;
        }
        final char begin = isRequired() ? '<' : '[';
        final char end = isRequired() ? '>' : ']';
        formatter.format("%s%s%s", begin, name, end);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("required", required)
            .add("description", description)
            .toString();
    }
}
