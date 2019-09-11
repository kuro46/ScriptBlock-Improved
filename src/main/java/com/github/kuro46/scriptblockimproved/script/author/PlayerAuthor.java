package com.github.kuro46.scriptblockimproved.script.author;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public final class PlayerAuthor {

    private final String name;
    private final UUID uniqueId;
    private final long lastUpdated;

    public PlayerAuthor(final String name, final UUID uniqueId, final long lastUpdated) {
        Objects.requireNonNull(name, "'name' cannot be null");
        Objects.requireNonNull(uniqueId, "'uniqueId' cannot be null");

        this.name = name;
        this.uniqueId = uniqueId;
        this.lastUpdated = lastUpdated;
    }

    public static PlayerAuthor fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        final String name = json.get("name").getAsString();
        final UUID uniqueId = UUID.fromString(json.get("uniqueId").getAsString());
        final long lastUpdated = json.get("lastUpdated").getAsLong();

        return new PlayerAuthor(name, uniqueId, lastUpdated);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("uniqueId", uniqueId.toString());
        json.addProperty("lastUpdated", lastUpdated);
        return json;
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    // TODO: PlayerAuthor updateIfNeeded() {}

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof PlayerAuthor)) return false;
        PlayerAuthor castedOther = (PlayerAuthor) other;

        return this.uniqueId.equals(castedOther.uniqueId);
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }
}
