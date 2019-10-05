package com.github.kuro46.scriptblockimproved.script.trigger;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class TriggerRegistry {

    private final Set<TriggerName> names = ConcurrentHashMap.newKeySet();

    public void register(@NonNull final String name) {
        register(TriggerName.of(name));
    }

    public void register(@NonNull final TriggerName name) {
        names.add(name);
    }

    public void unregister(@NonNull final String name) {
        unregister(TriggerName.of(name));
    }

    public void unregister(@NonNull final TriggerName name) {
        names.remove(name);
    }

    public boolean isRegistered(@NonNull final String name) {
        return isRegistered(TriggerName.of(name));
    }

    public boolean isRegistered(@NonNull final TriggerName name) {
        return names.contains(name);
    }

    public ImmutableSet<TriggerName> getView() {
        return ImmutableSet.copyOf(names);
    }
}
