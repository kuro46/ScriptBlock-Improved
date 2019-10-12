package com.github.kuro46.scriptblockimproved.script.trigger;

import com.google.errorprone.annotations.Immutable;
import com.google.gson.JsonPrimitive;
import java.util.Objects;

@Immutable
public final class TriggerName {

    private final String name;

    private TriggerName(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        this.name = name.toLowerCase();
    }

    public static TriggerName of(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        return new TriggerName(name);
    }

    public static TriggerName fromJson(final JsonPrimitive json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        return new TriggerName(json.getAsString());
    }

    public JsonPrimitive toJson() {
        return new JsonPrimitive(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof TriggerName)) {
            return false;
        }
        final TriggerName castedOther = (TriggerName) other;

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
