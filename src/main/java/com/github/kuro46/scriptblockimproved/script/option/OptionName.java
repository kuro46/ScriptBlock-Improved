package com.github.kuro46.scriptblockimproved.script.option;

import com.google.gson.JsonPrimitive;
import java.util.Objects;

public final class OptionName implements Comparable<OptionName> {

    private final String name;

    private OptionName(final String name) {
        this.name = name.toLowerCase();
    }

    public static OptionName of(final String name) {
        return new OptionName(name);
    }

    public static OptionName fromJson(final JsonPrimitive json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        return of(json.getAsString());
    }

    public JsonPrimitive toJson() {
        return new JsonPrimitive(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(OptionName other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof OptionName)) return false;
        OptionName castedOther = (OptionName) other;
        return this.name.equals(castedOther.name);
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
