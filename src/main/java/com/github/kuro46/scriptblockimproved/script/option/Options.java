package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.Placeholders;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.entity.Player;

public final class Options {

    private final ImmutableList<Option> options;

    public Options(final List<Option> options) {
        this.options = ImmutableList.copyOf(options);
    }

    public static Optional<Options> parse(final OptionHandlers handlers, final String str) {
        Objects.requireNonNull(handlers, "'handlers' cannot be null");
        Objects.requireNonNull(str, "'str' cannot be null");

        return OptionParser.parse(handlers, str);
    }

    public static Options fromJson(final JsonArray json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        final List<Option> options = new ArrayList<>();
        json.forEach(element -> options.add(Option.fromJson((JsonObject) element)));
        return new Options(options);
    }

    public JsonArray toJson() {
        final JsonArray json = new JsonArray();
        options.forEach(option -> json.add(option.toJson()));
        return json;
    }

    public Options replaced(
            final Placeholders placeholders,
            final Player player,
            final BlockCoordinate coordinate) {
        final List<Option> replaced = options.stream()
            .map(option -> option.replaced(placeholders, player, coordinate))
            .collect(Collectors.toList());
        return new Options(replaced);
    }

    public void forEach(final Consumer<Option> consumer) {
        options.forEach(consumer);
    }

    public Stream<Option> stream() {
        return options.stream();
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Options)) return false;
        final Options castedOther = (Options) other;

        return this.options.equals(castedOther.options);
    }

    @Override
    public int hashCode() {
        return options.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("options", options)
            .toString();
    }
}
