package com.github.kuro46.scriptblockimproved.script.author;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.UUID;

public final class Author {

    private final AuthorData data;

    private Author(final AuthorData data) {
        this.data = Objects.requireNonNull(data, "'data' cannot be null");
    }

    public static Author player(final String name, final UUID uniqueId) {
        return new Author(new PlayerAuthorData(name, uniqueId));
    }

    public static Author system() {
        return new Author(SystemAuthorData.getInstance());
    }

    public static Author console() {
        return new Author(ConsoleAuthorData.getInstance());
    }

    public static Author fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        final AuthorData data;
        final String type = json.get("type").getAsString();
        switch (type) {
            case "system":
                data = SystemAuthorData.getInstance();
                break;
            case "console":
                data = ConsoleAuthorData.getInstance();
                break;
            case "player":
                final JsonObject jsonData = json.getAsJsonObject("data");
                final String name = jsonData.get("name").getAsString();
                final UUID uniqueId = UUID.fromString(jsonData.get("uniqueId").getAsString());
                data = new PlayerAuthorData(name, uniqueId);
                break;
            default:
                throw new IllegalArgumentException("Unknown author type: " + type);
        }

        return new Author(data);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        final String type;
        if (data instanceof SystemAuthorData) {
            type = "system";
        } else if (data instanceof ConsoleAuthorData) {
            type = "console";
        } else if (data instanceof PlayerAuthorData) {
            type = "player";
            final JsonObject data = new JsonObject();
            final PlayerAuthorData playerData = (PlayerAuthorData) this.data;
            data.addProperty("name", playerData.getName());
            data.addProperty("uniqueId", playerData.getUniqueId().toString());
            json.add("data", data);
        } else {
            throw new IllegalArgumentException("Unknown data class: " + data.getClass());
        }

        json.addProperty("type", type);
        return json;
    }

    public String getName() {
        return data.getName();
    }

    public boolean isSystem() {
        return data instanceof SystemAuthorData;
    }

    public boolean isPlayer() {
        return data instanceof PlayerAuthorData;
    }

    public boolean isConsole() {
        return data instanceof ConsoleAuthorData;
    }

    public AuthorData getData() {
        return data;
    }
}
