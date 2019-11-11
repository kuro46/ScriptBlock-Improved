package com.github.kuro46.scriptblockimproved.script.trigger;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

public final class TriggerRegistry {

    private final Map<TriggerName, Trigger<?>> triggers = new HashMap<>();

    // For constructor of Trigger
    void register(@NonNull final Trigger<?> trigger) {
        final TriggerName name = trigger.getName();
        if (triggers.containsKey(name)) {
            final String message = String.format("Trigger '%s' is already registered", name);
            throw new TriggerRegistrationException(message);
        }
        triggers.put(name, trigger);
    }

    public void unregister(@NonNull final String name) {
        unregister(TriggerName.of(name));
    }

    public void unregister(@NonNull final TriggerName name) {
        final Trigger<?> removed = triggers.remove(name);
        if (removed == null) { // Trigger is not registered
            final String message = String.format("Trigger '%s' is not registered", name);
            throw new TriggerRegistrationException(message);
        }
        removed.unhookBukkit();
    }

    public boolean isRegistered(@NonNull final String name) {
        return isRegistered(TriggerName.of(name));
    }

    public boolean isRegistered(@NonNull final TriggerName name) {
        return triggers.containsKey(name);
    }

    public ImmutableMap<TriggerName, Trigger<?>> getView() {
        return ImmutableMap.copyOf(triggers);
    }
}
