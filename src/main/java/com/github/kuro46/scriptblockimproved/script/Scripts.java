package com.github.kuro46.scriptblockimproved.script;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.ToString;

@ToString
public final class Scripts {

    private final List<ScriptsListener> listeners = new ArrayList<>();
    @NonNull
    private volatile ImmutableListMultimap<BlockPosition, Script> scripts =
        ImmutableListMultimap.of();

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
            .map(Script::toJson)
            .forEach(json::add);
        return json;
    }

    public ImmutableListMultimap<BlockPosition, Script> getView() {
        return scripts;
    }

    public ImmutableSet<BlockPosition> getPositions() {
        return scripts.keySet();
    }

    public ImmutableList<Script> get(@NonNull final BlockPosition position) {
        return scripts.get(position);
    }

    private void compute(@NonNull Consumer<ListMultimap<BlockPosition, Script>> modifier) {
        final ListMultimap<BlockPosition, Script> mutable = ArrayListMultimap.create(scripts);
        modifier.accept(mutable);
        this.scripts = ImmutableListMultimap.copyOf(mutable);
    }

    public void add(@NonNull final Script script) {
        compute(mutable -> mutable.put(script.getPosition(), script));
        listeners.forEach(listener -> listener.onModified(this));
    }

    public void removeAll(@NonNull final BlockPosition position) {
        if (!scripts.containsKey(position)) {
            throw new IllegalArgumentException(
                    String.format("Script not exists at '%s'", position));
        }
        compute(mutable -> mutable.removeAll(position));
        listeners.forEach(listener -> listener.onModified(this));
    }

    public boolean contains(@NonNull final BlockPosition position) {
        return scripts.containsKey(position);
    }
}
