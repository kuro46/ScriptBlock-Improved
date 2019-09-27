package com.github.kuro46.scriptblockimproved.script;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Scripts {

    private final List<ScriptsListener> listeners = new ArrayList<>();
    private final ConcurrentMap<BlockPosition, List<Script>> scripts;

    public Scripts() {
        this(Collections.emptyMap());
    }

    public Scripts(final Map<BlockPosition, List<Script>> scripts) {
        Objects.requireNonNull(scripts, "'scripts' cannot be null");

        this.scripts = new ConcurrentHashMap<>(scripts);
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
        scripts.values().stream()
            .flatMap(Collection::stream)
            .forEach(script -> json.add(script.toJson()));
        return json;
    }

    public Set<BlockPosition> getPositions() {
        return scripts.keySet();
    }

    public void add(final Script script) {
        Objects.requireNonNull(script, "'script' cannot be null");

        final List<Script> scripts = this.scripts.computeIfAbsent(
            script.getPosition(),
            position -> new CopyOnWriteArrayList<>());
        scripts.add(script);
        listeners.forEach(listener -> listener.onModified(this));
    }

    public void removeAll(final BlockPosition position) {
        Objects.requireNonNull(position, "'position' cannot be null");

        if (!scripts.containsKey(position)) {
            throw new IllegalArgumentException(
                    String.format("Script not exists at '%s'", position));
        }
        scripts.remove(position);

        listeners.forEach(listener -> listener.onModified(this));
    }

    public boolean contains(final BlockPosition position) {
        return scripts.containsKey(position);
    }

    public ImmutableList<Script> get(final BlockPosition position) {
        Objects.requireNonNull(position, "'position' cannot be null");
        return ImmutableList.copyOf(scripts.get(position));
    }
}
