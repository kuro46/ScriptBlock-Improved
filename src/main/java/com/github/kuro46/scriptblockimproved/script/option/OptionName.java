package com.github.kuro46.scriptblockimproved.script.option;

import com.google.gson.JsonPrimitive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public final class OptionName implements Comparable<OptionName> {

    @Getter
    @NonNull
    private final String name;

    private OptionName(@NonNull final String name) {
        this.name = name.toLowerCase();
    }

    public static OptionName of(final String name) {
        return new OptionName(name);
    }

    public static OptionName fromJson(@NonNull final JsonPrimitive json) {
        return of(json.getAsString());
    }

    public JsonPrimitive toJson() {
        return new JsonPrimitive(name);
    }

    @Override
    public int compareTo(OptionName other) {
        return this.name.compareTo(other.name);
    }
}
