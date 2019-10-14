package com.github.kuro46.scriptblockimproved.script;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.ToString;

@ToString
public final class Scripts {

    private final Lock modifyLock = new ReentrantLock();
    private final List<ScriptsListener> listeners = new CopyOnWriteArrayList<>();
    @NonNull
    private volatile ImmutableListMultimap<BlockPosition, Script> scripts;

    public Scripts() {
        this.scripts = ImmutableListMultimap.of();
    }

    public Scripts(@NonNull final ListMultimap<BlockPosition, Script> initial) {
        this.scripts = ImmutableListMultimap.copyOf(initial);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Scripts fromJson(@NonNull final JsonArray json) {
        final Scripts.Builder builder = Scripts.builder();
        json.forEach(element -> builder.add(Script.fromJson((JsonObject) element)));
        return builder.build();
    }

    public JsonArray toJson() {
        final JsonArray json = new JsonArray();
        scripts.values().stream()
            .map(Script::toJson)
            .forEach(json::add);
        return json;
    }

    public void addListener(@NonNull final ScriptsListener listener) {
        listeners.add(listener);
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
        final Lock lock = modifyLock;
        lock.lock();
        try {
            final ListMultimap<BlockPosition, Script> mutable = ArrayListMultimap.create(scripts);
            modifier.accept(mutable);
            this.scripts = ImmutableListMultimap.copyOf(mutable);
        } finally {
            lock.unlock();
        }
    }

    public void add(@NonNull final Script script) {
        compute(mutable -> mutable.put(script.getPosition(), script));
        listeners.forEach(listener -> listener.onModified(this));
    }

    public void addAll(@NonNull final Scripts scripts) {
        compute(mutable -> mutable.putAll(scripts.scripts));
        listeners.forEach(listener -> listener.onModified(this));
    }

    public void removeAll(@NonNull final BlockPosition position) {
        compute(mutable -> {
            final List<Script> removed = mutable.removeAll(position);
            if (removed.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("Script not exists at '%s'", position));
            }
        });
        listeners.forEach(listener -> listener.onModified(this));
    }

    public boolean contains(@NonNull final BlockPosition position) {
        return scripts.containsKey(position);
    }

    public static final class Builder {

        private final ListMultimap<BlockPosition, Script> multimap = ArrayListMultimap.create();

        public Builder add(@NonNull final Script script) {
            multimap.put(script.getPosition(), script);
            return this;
        }

        public Scripts build() {
            return new Scripts(multimap);
        }
    }
}
