package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.Placeholders;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.entity.Player;

public final class Arguments {

    private final ImmutableMap<String, String> arguments;

    public Arguments(final Map<String, String> arguments) {
        Objects.requireNonNull(arguments, "'arguments' cannot be null");

        this.arguments = ImmutableMap.copyOf(arguments);
    }

    public static Arguments fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        final Map<String, String> arguments = new HashMap<>();
        json.entrySet().forEach(entry -> {
            final String name = entry.getKey();
            final String value = entry.getValue().getAsString();

            arguments.put(name, value);
        });
        return new Arguments(arguments);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();

        arguments.forEach(json::addProperty);
        return json;
    }

    public Arguments replaced(
            final Placeholders placeholders,
            final Player player,
            final BlockCoordinate coordinate) {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        arguments.forEach((key, value) -> {
            builder.put(key, placeholders.replace(value, player, coordinate));
        });
        return new Arguments(builder.build());
    }

    public Optional<String> get(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        return Optional.ofNullable(arguments.get(name));
    }

    public String getOrNull(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        return get(name).orElse(null);
    }

    public String getOrFail(final String name) {
        Objects.requireNonNull(name, "'name' cannot be null");

        return get(name).orElseThrow(() -> {
            return new IllegalArgumentException(
                    String.format("Argument named '%s' not exists", name));
        });
    }

    public Map<String, String> toMutable() {
        return new HashMap<>(arguments);
    }

    public ImmutableMap<String, String> getView() {
        return arguments;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Arguments)) {
            return false;
        }
        final Arguments castedOther = (Arguments) other;

        return this.arguments.equals(castedOther.arguments);
    }

    @Override
    public int hashCode() {
        return arguments.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("arguments", arguments)
            .toString();
    }
}
