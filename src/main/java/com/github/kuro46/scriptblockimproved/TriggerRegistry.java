package com.github.kuro46.scriptblockimproved;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;

public final class TriggerRegistry {

    private final Map<String, Trigger> byName = new HashMap<>();
    private final Map<Integer, Trigger> byId = new HashMap<>();

    public void register(@NonNull final Trigger trigger) {
        if (byName.containsKey(trigger.getName())) {
            throw new IllegalArgumentException(String.format("Trigger '%s' is already registered!", trigger.getName()));
        }

        byName.put(trigger.getName().toLowerCase(Locale.ENGLISH), trigger);
        byId.put(trigger.getId(), trigger);
    }

    public void unregister(@NonNull final String triggerName) {
        final String name = triggerName.toLowerCase(Locale.ENGLISH);
        if (!byName.containsKey(name)) {
            throw new IllegalArgumentException(String.format("Unable to find trigger '%s'", triggerName));
        }
        final Trigger trigger = byName.remove(name);
        byId.remove(trigger.getId());
        trigger.unregister();
    }

    public boolean isRegistered(@NonNull final Trigger trigger) {
        return byId.containsKey(trigger.getId());
    }

    public Optional<Trigger> getTrigger(@NonNull final String name) {
        return Optional.ofNullable(byName.get(name.toLowerCase(Locale.ENGLISH)));
    }

    public Optional<Trigger> getTrigger(final int id) {
        return Optional.ofNullable(byId.get(id));
    }

    public ImmutableList<Trigger> getTriggers() {
        return ImmutableList.copyOf(byName.values());
    }
}
