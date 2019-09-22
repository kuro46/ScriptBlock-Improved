package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.command.ArgName;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.Placeholders;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Objects;
import org.bukkit.entity.Player;

public final class Option {

    private final OptionName name;
    private final ParsedArgs args;

    public Option(final OptionName name, final ParsedArgs args) {
        Objects.requireNonNull(name, "'name' cannot be null");
        Objects.requireNonNull(args, "'args' cannot be null");

        this.name = name;
        this.args = args;
    }

    public static Option fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        final OptionName name = OptionName.fromJson(json.getAsJsonPrimitive("name"));

        final JsonObject jsonArgs = json.getAsJsonObject("args");
        final ImmutableMap.Builder<ArgName, String> argsBuilder = ImmutableMap.builder();
        for (Map.Entry<String, JsonElement> entry : jsonArgs.entrySet()) {
            argsBuilder.put(ArgName.of(entry.getKey()), entry.getValue().getAsString());
        }

        return new Option(name, new ParsedArgs(argsBuilder.build()));
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.add("name", name.toJson());
        final JsonObject argsJson = new JsonObject();
        args.asMap().forEach((key, value) -> {
            argsJson.addProperty(key.getName(), value);
        });
        json.add("args", argsJson);
        return json;
    }

    public Option replaced(
            final Placeholders placeholders,
            final Player player,
            final BlockCoordinate coordinate) {
        final ImmutableMap.Builder<ArgName, String> builder = ImmutableMap.builder();
        args.asMap().forEach((key, value) -> {
            builder.put(key, placeholders.replace(value, player, coordinate));
        });
        return new Option(name, new ParsedArgs(builder.build()));
    }

    public OptionName getName() {
        return name;
    }

    public ParsedArgs getArgs() {
        return args;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Option)) {
            return false;
        }
        final Option castedOther = (Option) other;

        return this.name.equals(castedOther.name)
            && this.args.equals(castedOther.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("args", args)
            .toString();
    }
}
