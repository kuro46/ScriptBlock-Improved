package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.google.common.base.MoreObjects;
import com.google.gson.JsonObject;
import java.util.Objects;

public final class Script {

    private final long createdAt;
    private final TriggerName trigger;
    private final Author author;
    private final BlockPosition position;
    private final Options options;

    public Script(
            final long createdAt,
            final TriggerName trigger,
            final Author author,
            final BlockPosition position,
            final Options options) {
        Objects.requireNonNull(trigger, "'trigger' cannot be null");
        Objects.requireNonNull(author, "'author' cannot be null");
        Objects.requireNonNull(position, "'position' cannot be null");
        Objects.requireNonNull(options, "'options' cannot be null");

        this.createdAt = createdAt;
        this.trigger = trigger;
        this.author = author;
        this.position = position;
        this.options = options;
    }

    public static Script fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

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

    public TriggerName getTrigger() {
        return trigger;
    }

    public Author getAuthor() {
        return author;
    }

    public BlockPosition getPosition() {
        return position;
    }

    public Options getOptions() {
        return options;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Script)) return false;
        Script castedOther = (Script) other;

        return this.author.equals(castedOther.author)
                && this.position.equals(castedOther.position)
                && this.options.equals(castedOther.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, position, options);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("author", author)
            .add("position", position)
            .add("options", options)
            .toString();
    }
}
