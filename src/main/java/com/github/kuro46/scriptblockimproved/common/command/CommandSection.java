package com.github.kuro46.scriptblockimproved.common.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public final class CommandSection {

    @NonNull
    @Getter
    private final String name;

    public CommandSection(@NonNull final String name) {
        this.name = name.toLowerCase();
    }

    public static CommandSection of(final String name) {
        return new CommandSection(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
