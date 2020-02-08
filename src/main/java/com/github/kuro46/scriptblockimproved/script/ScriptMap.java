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
import lombok.NonNull;
import lombok.ToString;

/**
 * A ScriptMap that holds BlockPosition-Script pair as ListMultimap.
 */
@ToString
public final class ScriptMap {

    private final Lock lock = new ReentrantLock();
    private final List<ScriptMapListener> listeners = new CopyOnWriteArrayList<>();
    private final ListMultimap<BlockPosition, Script> map = ArrayListMultimap.create();

    /**
     * Constructs a ScriptMap with empty scripts.
     */
    public ScriptMap() {
    }

    /**
     * Constructs a ScriptMap with the specified scripts.
     *
     * @param initial scripts
     */
    public ScriptMap(@NonNull final ListMultimap<BlockPosition, Script> initial) {
        this.map.putAll(initial);
    }

    /**
     * Constructs a builder for ScriptMap.
     *
     * @return Builder for this class
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Constructs a ScriptMap from a JsonArray.
     *
     * @param json JsonArray
     * @return ScriptMap that contains all entries in the JsonArray
     */
    public static ScriptMap fromJson(@NonNull final JsonArray json) {
        final ScriptMap.Builder builder = ScriptMap.builder();
        json.forEach(element -> builder.add(Script.fromJson((JsonObject) element)));
        return builder.build();
    }

    /**
     * Serialize this instance to JsonArray.
     *
     * @return JsonArray that contains all entries in this map
     */
    public JsonArray toJson() {
        final JsonArray json = new JsonArray();
        snapshot().values().stream()
            .map(Script::toJson)
            .forEach(json::add);
        return json;
    }

    /**
     * Add the specified listener to listen this map.
     *
     * @param listener ScriptMapListener to add
     */
    public void addListener(@NonNull final ScriptMapListener listener) {
        listeners.add(listener);
    }

    /**
     * Returns ScriptMap as ListMultimap.
     *
     * @return ImmutableListMultimap which contains all entries of this ScriptMap
     */
    public ImmutableListMultimap<BlockPosition, Script> asListMultimap() {
        return snapshot();
    }

    /**
     * Returns all positions.
     *
     * @return ImmutableSet
     */
    public ImmutableSet<BlockPosition> getPositions() {
        return snapshot().keySet();
    }

    /**
     * Returns all scripts that added in the specified position.
     *
     * @param position position to get
     * @return ImmutableSet. If no scripts were added, it will return an empty list.
     */
    public ImmutableList<Script> get(@NonNull final BlockPosition position) {
        lock.lock();
        try {
            return ImmutableList.copyOf(map.get(position));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Creates a immutable snapshot of map.
     */
    private ImmutableListMultimap<BlockPosition, Script> snapshot() {
        return ImmutableListMultimap.copyOf(map);
    }

    /**
     * Adds the specified script to this map.
     *
     * @param script Script to add
     */
    public void add(@NonNull final Script script) {
        lock.lock();
        try {
            map.put(script.getPosition(), script);
        } finally {
            lock.unlock();
        }
        listeners.forEach(listener -> listener.onModified(this));
    }

    /**
     * Adds all scripts in the specified ScriptMap to this map.
     *
     * @param scripts ScriptMap to add
     */
    public void addAll(@NonNull final ScriptMap scripts) {
        lock.lock();
        try {
            map.putAll(scripts.map);
        } finally {
            lock.unlock();
        }
        listeners.forEach(listener -> listener.onModified(this));
    }

    /**
     * Removes all scripts in the specified position.
     *
     * @param position position to remove
     * @throws IllegalArgumentException if no scripts found for the specified position
     */
    public void removeAll(@NonNull final BlockPosition position) {
        lock.lock();
        try {
            final List<Script> removed = map.removeAll(position);
            if (removed.isEmpty()) {
                throw new IllegalArgumentException(
                        String.format("Script not exists at '%s'", position));
            }
        } finally {
            lock.unlock();
        }
        listeners.forEach(listener -> listener.onModified(this));
    }

    /**
     * Returns the existence of scripts in the specified position.
     *
     * @param position position to check existence
     * @return existence of scripts in the specified position.
     */
    public boolean contains(@NonNull final BlockPosition position) {
        lock.lock();
        try {
            return map.containsKey(position);
        } finally {
            lock.unlock();
        }
    }

    /**
     * A Builder for build ScriptMap
     */
    public static final class Builder {

        private final ListMultimap<BlockPosition, Script> multimap = ArrayListMultimap.create();

        /**
         * Adds the specified script to this builder.
         *
         * @param script Script instance to add
         * @return this instance
         */
        public Builder add(@NonNull final Script script) {
            multimap.put(script.getPosition(), script);
            return this;
        }

        /**
         * Builds ScriptMap instance from this builder.
         *
         * @return built ScriptMap
         */
        public ScriptMap build() {
            return new ScriptMap(multimap);
        }
    }
}
