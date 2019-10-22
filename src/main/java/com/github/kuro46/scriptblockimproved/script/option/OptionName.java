package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.Name;
import com.google.gson.JsonPrimitive;
import lombok.NonNull;

/**
 * A OptionName represents a name of option.
 */
public final class OptionName extends Name {

    private OptionName(@NonNull final String name) {
        super(name);
    }

    public static OptionName of(@NonNull final String name) {
        return new OptionName(name);
    }

    public static OptionName fromJson(@NonNull final JsonPrimitive json) {
        return of(json.getAsString());
    }

    public JsonPrimitive toJson() {
        return new JsonPrimitive(string);
    }
}
