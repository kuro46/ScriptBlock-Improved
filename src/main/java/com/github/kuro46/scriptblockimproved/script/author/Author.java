package com.github.kuro46.scriptblockimproved.script.author;

import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class Author {

    private final AuthorType type;
    private final PlayerAuthor playerAuthor;

    private Author(final AuthorType type, final PlayerAuthor playerAuthor) {
        Objects.requireNonNull(type, "'type' cannot be null");

        this.type = type;
        this.playerAuthor = playerAuthor;
    }

    public static Author player(final String name, final UUID uniqueId, final long lastUpdated) {
        Objects.requireNonNull(name, "'name' cannot be null");
        Objects.requireNonNull(uniqueId, "'uniqueId' cannot be null");

        return new Author(AuthorType.PLAYER, new PlayerAuthor(name, uniqueId, lastUpdated));
    }

    public static Author player(final String name, final UUID uniqueId) {
        return player(name, uniqueId, System.currentTimeMillis());
    }

    public static Author system() {
        return new Author(AuthorType.SYSTEM, null);
    }

    public static Author console() {
        return new Author(AuthorType.CONSOLE, null);
    }

    public static Author fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        final AuthorType type = AuthorType.valueOf(json.get("type").getAsString().toUpperCase());
        final PlayerAuthor playerAuthor;
        if (type == AuthorType.PLAYER) {
            playerAuthor = PlayerAuthor.fromJson(json.getAsJsonObject("playerData"));
        } else {
            playerAuthor = null;
        }
        return new Author(type, playerAuthor);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("type", type.name());
        if (isPlayer()) {
            json.add("playerData", playerAuthor.toJson());
        }
        return json;
    }

    public String getName() {
        switch (type) {
            case SYSTEM:
                return "SYSTEM";
            case CONSOLE:
                return "CONSOLE";
            case PLAYER:
                return playerAuthor.getName();
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isSystem() {
        return type == AuthorType.SYSTEM;
    }

    public boolean isPlayer() {
        return type == AuthorType.PLAYER;
    }

    public boolean isConsole() {
        return type == AuthorType.CONSOLE;
    }

    public Optional<PlayerAuthor> getAsPlayer() {
        return Optional.ofNullable(playerAuthor);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Author)) return false;
        Author castedOther = (Author) other;

        if (playerAuthor == null) {
            return castedOther.playerAuthor == null;
        }

        return this.playerAuthor.equals(castedOther.playerAuthor);
    }

    @Override
    public int hashCode() {
        if (playerAuthor != null) {
            return playerAuthor.hashCode();
        } else {
            return 0;
        }
    }
}
