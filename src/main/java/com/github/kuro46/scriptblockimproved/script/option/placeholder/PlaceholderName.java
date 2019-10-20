package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public final class PlaceholderName {

    @Getter
    @NonNull
    private final String name;

    public PlaceholderName(@NonNull final String name) {
        this.name = name.toLowerCase();
    }

    public static PlaceholderName of(final String name) {
        return new PlaceholderName(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
