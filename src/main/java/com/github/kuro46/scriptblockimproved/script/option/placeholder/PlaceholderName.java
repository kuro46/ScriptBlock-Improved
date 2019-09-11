package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import java.util.Objects;

public final class PlaceholderName {

    private final String name;

    public PlaceholderName(final String name) {
        this.name = Objects.requireNonNull(name, "'name' cannot be null").toLowerCase();
    }

    public static PlaceholderName of(final String name) {
        return new PlaceholderName(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof PlaceholderName)) {
            return false;
        }
        final PlaceholderName castedOther = (PlaceholderName) other;

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
