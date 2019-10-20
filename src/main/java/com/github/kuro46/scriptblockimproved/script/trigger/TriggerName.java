package com.github.kuro46.scriptblockimproved.script.trigger;

import com.google.errorprone.annotations.Immutable;
import com.google.gson.JsonPrimitive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
@Immutable
public final class TriggerName {

    @Getter
    @NonNull
    private final String name;

    private TriggerName(@NonNull final String name) {
        this.name = name.toLowerCase();
    }

    public static TriggerName of(@NonNull final String name) {
        return new TriggerName(name);
    }

    public static TriggerName fromJson(@NonNull final JsonPrimitive json) {
        return new TriggerName(json.getAsString());
    }

    public JsonPrimitive toJson() {
        return new JsonPrimitive(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
