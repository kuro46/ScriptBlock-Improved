package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.Placeholders;
import com.google.common.base.MoreObjects;
import com.google.gson.JsonObject;
import java.util.Objects;
import org.bukkit.entity.Player;

public final class Option {

    private final OptionName name;
    private final Arguments arguments;

    public Option(final OptionName name, final Arguments arguments) {
        Objects.requireNonNull(name, "'name' cannot be null");
        Objects.requireNonNull(arguments, "'arguments' cannot be null");

        this.name = name;
        this.arguments = arguments;
    }

    public static Option fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        final OptionName name = OptionName.fromJson(json.getAsJsonPrimitive("name"));
        final Arguments arguments = Arguments.fromJson(json.getAsJsonObject("arguments"));
        return new Option(name, arguments);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.add("name", name.toJson());
        json.add("arguments", arguments.toJson());
        return json;
    }

    public Option replaced(
            final Placeholders placeholders,
            final Player player,
            final BlockCoordinate coordinate) {
        return new Option(name, arguments.replaced(placeholders, player, coordinate));
    }

    public OptionName getName() {
        return name;
    }

    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Option)) {
            return false;
        }
        final Option castedOther = (Option) other;

        return this.name.equals(castedOther.name)
            && this.arguments.equals(castedOther.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("arguments", arguments)
            .toString();
    }
}
