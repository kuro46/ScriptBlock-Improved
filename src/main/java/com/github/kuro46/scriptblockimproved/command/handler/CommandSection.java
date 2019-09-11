package com.github.kuro46.scriptblockimproved.command.handler;

import java.util.Objects;

public final class CommandSection {

    private final String name;

    public CommandSection(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        this.name = name.toLowerCase();
    }

    public static CommandSection of(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        return new CommandSection(name);
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
