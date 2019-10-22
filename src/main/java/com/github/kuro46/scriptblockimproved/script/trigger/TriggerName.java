package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.common.Name;
import com.google.gson.JsonPrimitive;
import lombok.NonNull;

public final class TriggerName extends Name {

    private TriggerName(@NonNull final String name) {
        super(name);
    }

    public static TriggerName of(@NonNull final String name) {
        return new TriggerName(name);
    }

    public static TriggerName fromJson(@NonNull final JsonPrimitive json) {
        return new TriggerName(json.getAsString());
    }

    public JsonPrimitive toJson() {
        return new JsonPrimitive(string);
    }
}
