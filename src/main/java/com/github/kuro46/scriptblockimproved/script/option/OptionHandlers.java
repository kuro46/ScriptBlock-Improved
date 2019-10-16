package com.github.kuro46.scriptblockimproved.script.option;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;

public final class OptionHandlers {

    private final Map<OptionName, OptionHandler> handlers = new HashMap<>();

    public ImmutableSet<OptionName> names() {
        return ImmutableSet.copyOf(handlers.keySet());
    }

    public boolean contains(@NonNull final OptionName key) {
        return handlers.containsKey(key);
    }

    public void add(
            @NonNull final OptionName key,
            @NonNull final OptionHandler handler) {
        if (contains(key)) {
            throw new IllegalArgumentException(
                    String.format("A handler for '%s' has already added", key));
        }
        handlers.put(key, handler);
    }

    public Optional<OptionHandler> get(@NonNull final OptionName key) {
        return Optional.ofNullable(handlers.get(key));
    }

    public OptionHandler getOrFail(@NonNull final OptionName key) {
        return get(key).orElseThrow(() -> new IllegalArgumentException(
                String.format("A handler for '%s' not exists", key)));
    }
}
