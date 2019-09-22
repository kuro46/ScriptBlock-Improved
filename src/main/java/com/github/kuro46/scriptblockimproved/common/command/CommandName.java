package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Formattable;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Name of Command<br>
 *
 * <pre>
 * /section1 section2 section3...
 * \-------------name-----------/
 * </pre>
 */
public final class CommandName implements Formattable, Comparable<CommandName> {

    private final ImmutableList<CommandSection> sections;

    private CommandName(final List<CommandSection> sections) {
        Objects.requireNonNull(sections, "'sections' cannot be null");
        this.sections = ImmutableList.copyOf(sections);
    }

    public static CommandName of(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        return new CommandName(Arrays.stream(name.split(" "))
                .map(CommandSection::new)
                .collect(Collectors.toList()));
    }

    public static CommandName fromSections(final List<CommandSection> sections) {
        Objects.requireNonNull(sections, "'sections' cannot be null");
        return new CommandName(sections);
    }

    public static CommandName fromStrings(final List<String> strings) {
        return CommandName.fromSections(ImmutableList.copyOf(strings.stream()
            .map(CommandSection::new)
            .collect(Collectors.toList())));
    }

    public ImmutableList<CommandSection> asSections() {
        return sections;
    }

    private String joined() {
        return sections.stream()
            .map(CommandSection::getName)
            .collect(Collectors.joining(" "));
    }

    @Override
    public int compareTo(final CommandName other) {
        return joined().compareTo(other.joined());
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof CommandName)) {
            return false;
        }
        final CommandName castedOther = (CommandName) other;

        return sections.equals(castedOther.sections);
    }

    @Override
    public int hashCode() {
        return sections.hashCode();
    }

    @Override
    public void formatTo(
            final Formatter formatter,
            final int flags,
            final int width,
            final int precision) {
        formatter.format("%s", joined());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("sections", sections)
            .toString();
    }
}
