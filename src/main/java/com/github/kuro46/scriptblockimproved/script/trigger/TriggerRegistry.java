package com.github.kuro46.scriptblockimproved.script.trigger;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

public final class TriggerRegistry {

    private final Map<TriggerName, RegisteredTrigger> triggers = new HashMap<>();

    public RegisteredTrigger register(@NonNull final String name) {
        return register(TriggerName.of(name));
    }

    public RegisteredTrigger register(@NonNull final TriggerName name) {
        if (triggers.containsKey(name)) {
            final String message = String.format("Trigger '%s' is already registered", name);
            throw new TriggerRegistrationException(message);
        }
        final RegisteredTrigger trigger = new RegisteredTrigger(name);
        triggers.put(name, trigger);
        return trigger;
    }

    public void unregister(@NonNull final String name) {
        unregister(TriggerName.of(name));
    }

    public void unregister(@NonNull final TriggerName name) {
        final RegisteredTrigger removed = triggers.remove(name);
        if (removed == null) { // Trigger is not registered
            final String message = String.format("Trigger '%s' is not registered", name);
            throw new TriggerRegistrationException(message);
        }
        removed.unregister();
    }

    public boolean isRegistered(@NonNull final String name) {
        return isRegistered(TriggerName.of(name));
    }

    public boolean isRegistered(@NonNull final TriggerName name) {
        return triggers.containsKey(name);
    }

    public ImmutableMap<TriggerName, RegisteredTrigger> getView() {
        return ImmutableMap.copyOf(triggers);
    }
}
