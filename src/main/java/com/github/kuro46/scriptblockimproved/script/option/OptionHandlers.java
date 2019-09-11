package com.github.kuro46.scriptblockimproved.script.option;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class OptionHandlers {

    private final Map<OptionName, OptionHandler> handlers = new HashMap<>();

    public ImmutableSet<OptionName> names() {
        return ImmutableSet.copyOf(handlers.keySet());
    }

    public boolean contains(final OptionName key) {
        Objects.requireNonNull(key, "'key' cannot be null");

        return handlers.containsKey(key);
    }

    public void add(final OptionName key, final OptionHandler handler) {
        Objects.requireNonNull(key, "'key' cannot be null");
        Objects.requireNonNull(handler, "'handler' cannot be null");

        if (contains(key)) {
            throw new IllegalArgumentException(
                    String.format("A handler for '%s' has already added", key));
        }

        handlers.put(key, handler);
    }

    public Optional<OptionHandler> get(final OptionName key) {
        Objects.requireNonNull(key, "'key' cannot be null");

        return Optional.ofNullable(handlers.get(key));
    }

    public OptionHandler getOrFail(final OptionName key) {
        Objects.requireNonNull(key, "'key' cannot be null");

        return get(key).orElseThrow(() -> new IllegalArgumentException(
                String.format("A handler for '%s' not exists", key)));
    }
}
