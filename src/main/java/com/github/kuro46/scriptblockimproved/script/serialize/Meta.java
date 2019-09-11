package com.github.kuro46.scriptblockimproved.script.serialize;

import com.google.gson.JsonObject;
import java.util.Objects;

public final class Meta {

    private final String version;

    public Meta(final String version) {
        Objects.requireNonNull(version, "'version' cannot be null");

        this.version = version;
    }

    public static Meta fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        return new Meta(json.get("version").getAsString());
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("version", version);
        return json;
    }

    public String getVersion() {
        return version;
    }
}
