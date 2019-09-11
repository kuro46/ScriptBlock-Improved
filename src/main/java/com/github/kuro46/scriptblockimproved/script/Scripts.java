package com.github.kuro46.scriptblockimproved.script;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public final class Scripts {

    private final List<ScriptsListener> listeners = new ArrayList<>();
    private final ListMultimap<BlockCoordinate, Script> scripts;

    public Scripts() {
        this(ArrayListMultimap.create());
    }

    public Scripts(final ListMultimap<BlockCoordinate, Script> scripts) {
        Objects.requireNonNull(scripts, "'scripts' cannot be null");

        this.scripts = ArrayListMultimap.create(scripts);
    }

    public Scripts shallowCopy() {
        return new Scripts(scripts);
    }

    public void addListener(final ScriptsListener listener) {
        listeners.add(listener);
    }

    public static Scripts fromJson(final JsonArray json) {
        Objects.requireNonNull(json, "'json' cannot be null");

        final Scripts scripts = new Scripts();
        json.forEach(element -> scripts.add(Script.fromJson((JsonObject) element)));
        return scripts;
    }

    public JsonArray toJson() {
        final JsonArray json = new JsonArray();
        scripts.forEach((coordinate, script) -> json.add(script.toJson()));
        return json;
    }

    public Set<BlockCoordinate> getCoordinates() {
        return scripts.keySet();
    }

    public void forEach(final BiConsumer<BlockCoordinate, Script> consumer) {
        scripts.forEach(consumer);
    }

    public void add(final Script script) {
        Objects.requireNonNull(script, "'script' cannot be null");

        scripts.put(script.getCoordinate(), script);
        listeners.forEach(listener -> listener.onModified(this));
    }

    public void removeAll(final BlockCoordinate coordinate) {
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");

        if (!scripts.containsKey(coordinate)) {
            throw new IllegalArgumentException(
                    String.format("Script not exists at '%s'", coordinate));
        }
        scripts.removeAll(coordinate);

        listeners.forEach(listener -> listener.onModified(this));
    }

    public boolean contains(final BlockCoordinate coordinate) {
        return scripts.containsKey(coordinate);
    }

    public ImmutableList<Script> get(final BlockCoordinate coordinate) {
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");
        return ImmutableList.copyOf(scripts.get(coordinate));
    }
}
