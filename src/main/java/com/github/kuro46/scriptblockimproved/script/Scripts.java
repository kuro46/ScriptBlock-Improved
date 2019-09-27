package com.github.kuro46.scriptblockimproved.script;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.NonNull;

public final class Scripts {

    private final List<ScriptsListener> listeners = new ArrayList<>();
    private final ConcurrentMap<BlockPosition, CopyOnWriteArrayList<Script>> scripts =
        new ConcurrentHashMap<>();

    public void addListener(@NonNull final ScriptsListener listener) {
        listeners.add(listener);
    }

    public static Scripts fromJson(@NonNull final JsonArray json) {
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

    public void add(@NonNull final Script script) {
        final List<Script> scripts = this.scripts.computeIfAbsent(
            script.getPosition(),
            p -> new CopyOnWriteArrayList<>());
        scripts.add(script);
        listeners.forEach(listener -> listener.onModified(this));
    }

    public void removeAll(@NonNull final BlockPosition position) {
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

    public ImmutableList<Script> get(@NonNull final BlockPosition position) {
        return ImmutableList.copyOf(scripts.get(position));
    }
}
