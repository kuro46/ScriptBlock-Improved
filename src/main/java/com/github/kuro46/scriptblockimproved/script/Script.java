package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.OptionList;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.google.gson.JsonObject;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Builder(builderClassName = "Builder")
public final class Script {

    @Getter
    private final long createdAt;
    @Getter
    @NonNull
    private final TriggerName triggerName;
    @Getter
    @NonNull
    private final Author author;
    @Getter
    @NonNull
    private final BlockPosition position;
    @Getter
    @NonNull
    private final OptionList options;

    public Script(
            final long createdAt,
            @NonNull final TriggerName triggerName,
            @NonNull final Author author,
            @NonNull final BlockPosition position,
            @NonNull final OptionList options) {
        this.createdAt = createdAt;
        this.triggerName = triggerName;
        this.author = author;
        this.position = position;
        this.options = options;
    }

    public static Script fromJson(@NonNull final JsonObject json) {
        return Script.builder()
            .createdAt(json.get("createdAt").getAsLong())
            .triggerName(TriggerName.fromJson(json.getAsJsonPrimitive("triggerName")))
            .position(BlockPosition.fromJson(json.getAsJsonObject("position")))
            .author(Author.fromJson(json.getAsJsonObject("author")))
            .options(OptionList.fromJson(json.getAsJsonArray("options")))
            .build();
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("createdAt", createdAt);
        json.add("triggerName", triggerName.toJson());
        json.add("author", author.toJson());
        json.add("position", position.toJson());
        json.add("options", options.toJson());
        return json;
    }
}
