package com.github.kuro46.scriptblockimproved.common;

import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Immutable
@EqualsAndHashCode
public abstract class Name implements Comparable<Name> {

    @NonNull
    protected final String string;

    public Name(@NonNull final String name) {
        this.string = name.toLowerCase();
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public int compareTo(final Name other) {
        return this.string.compareTo(other.string);
    }
}
