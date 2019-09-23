package com.github.kuro46.scriptblockimproved.common.command;

import java.util.Objects;

public final class CommandSection {

    private final String name;

    public CommandSection(final String name) {
        this.name = Objects.requireNonNull(name, "'name' cannot be null")
            .toLowerCase();
    }

    public static CommandSection of(final String name) {
        return new CommandSection(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof CommandSection)) {
            return false;
        }
        final CommandSection castedOther = (CommandSection) other;

        return name.equals(castedOther.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
