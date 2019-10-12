package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public final class Script {

    @Getter
    private final long createdAt;
    @Getter
    @NonNull
    private final TriggerName trigger;
    @Getter
    @NonNull
    private final Author author;
    @Getter
    @NonNull
    private final BlockPosition position;
    @Getter
    @NonNull
    private final Options options;

    public Script(
            final long createdAt,
            @NonNull final TriggerName trigger,
            @NonNull final Author author,
            @NonNull final BlockPosition position,
            @NonNull final Options options) {
        this.createdAt = createdAt;
        this.trigger = trigger;
        this.author = author;
        this.position = position;
        this.options = options;
    }

    public static Script fromJson(@NonNull final JsonObject json) {
        final long createdAt = json.get("createdAt").getAsLong();
        final TriggerName trigger = TriggerName.fromJson(json.getAsJsonPrimitive("trigger"));
        final Author author = Author.fromJson(json.getAsJsonObject("author"));
        final BlockPosition position =
            BlockPosition.fromJson(json.getAsJsonObject("position"));
        final Options options = Options.fromJson(json.getAsJsonArray("options"));
        return new Script(createdAt, trigger, author, position, options);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("createdAt", createdAt);
        json.add("trigger", trigger.toJson());
        json.add("author", author.toJson());
        json.add("position", position.toJson());
        json.add("options", options.toJson());
        return json;
    }
}
