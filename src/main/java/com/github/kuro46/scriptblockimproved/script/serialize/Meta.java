package com.github.kuro46.scriptblockimproved.script.serialize;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;

public final class Meta {

    @Getter
    @NonNull
    private final String version;

    public Meta(@NonNull final String version) {
        this.version = version;
    }

    public static Meta fromJson(@NonNull final JsonObject json) {
        return new Meta(json.get("version").getAsString());
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("version", version);
        return json;
    }
}
