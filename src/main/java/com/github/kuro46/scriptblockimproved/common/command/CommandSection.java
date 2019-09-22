package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * Section of command<br>
 *
 * <pre>
 * /section1 sections2 sections3...
 * </pre>
 */
public final class CommandSection {

    /**
     * Name of this section
     */
    private final String name;

    /**
     * Constructs a section
     *
     * @param name Name of this section
     */
    public CommandSection(final String name) {
        this.name = Objects.requireNonNull(name, "'name' cannot be null")
            .toLowerCase();
    }

    /**
     * Returns name of this section
     *
     * @return Name of this section
     */
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
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .toString();
    }
}
