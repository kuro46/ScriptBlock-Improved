package com.github.kuro46.scriptblockimproved.command.handler;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CommandSections implements Iterable<CommandSection> {

    private final ImmutableList<CommandSection> sections;

    public CommandSections(final List<CommandSection> sections) {
        Objects.requireNonNull(sections, "'sections' cannot be null");

        this.sections = ImmutableList.copyOf(sections);
    }

    public static CommandSections of(final String sections) {
        Objects.requireNonNull(sections, "'sections' cannot be null");

        final List<CommandSection> list = Arrays.stream(sections.split(" "))
            .map(CommandSection::of)
            .collect(Collectors.toList());
        return new CommandSections(list);
    }

    public CommandSections parent() {
        final ImmutableList.Builder<CommandSection> builder = ImmutableList.builder();
        for (int i = 0; i < sections.size() - 1; i++) {
            builder.add(sections.get(i));
        }
        return new CommandSections(builder.build());
    }

    public CommandSections child(final String childName) {
        final ImmutableList<CommandSection> list = new ImmutableList.Builder<CommandSection>()
            .addAll(sections)
            .add(CommandSection.of(childName))
            .build();
        return new CommandSections(list);
    }

    @Override
    public Iterator<CommandSection> iterator() {
        return sections.iterator();
    }

    public ImmutableList<CommandSection> getView() {
        return sections;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof CommandSections)) {
            return false;
        }
        final CommandSections castedOther = (CommandSections) other;

        return sections.equals(castedOther.sections);
    }

    @Override
    public int hashCode() {
        return sections.hashCode();
    }

    @Override
    public String toString() {
        return sections.stream()
            .map(CommandSection::toString)
            .collect(Collectors.joining(" "));
    }
}
