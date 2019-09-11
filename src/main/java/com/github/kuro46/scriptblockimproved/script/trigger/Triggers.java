package com.github.kuro46.scriptblockimproved.script.trigger;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public final class Triggers {

    private static final Listener EMPTY_LISTENER = new Listener() {
    };

    private final List<TriggersListener> listeners = new ArrayList<>();
    private final Map<TriggerName, Trigger> byName = new HashMap<>();
    private final Plugin plugin;

    public Triggers(final Plugin plugin) {
        Objects.requireNonNull(plugin, "'plugin' cannot be null");

        this.plugin = plugin;
    }

    public void addListener(final TriggersListener listener) {
        listeners.add(listener);
    }

    public ImmutableList<Trigger> getTriggers() {
        return ImmutableList.copyOf(byName.values());
    }

    public Optional<Trigger> getByName(final TriggerName name) {
        return Optional.ofNullable(byName.get(name));
    }

    public boolean isRegisteredForName(final TriggerName name) {
        return byName.containsKey(name);
    }

    public void register(final Trigger trigger) {
        if (isRegisteredForName(trigger.getName())) {
            throw new IllegalArgumentException(
                    String.format("A trigger named '%s' already registered", trigger.getName()));
        }

        byName.put(trigger.getName(), trigger);

        registerEvent(trigger);
    }

    private void registerEvent(final Trigger trigger) {
        final EventExecutor executor = (listener, event) -> {
            final EventValidateResult result = trigger.validate(event);
            if (result.isValid()) {
                listeners.forEach(triggersListener -> {
                    final EventData data = result.getEventData();
                    triggersListener.onValidEvent(
                            trigger,
                            event,
                            data.getPlayer(),
                            data.getCoordinate());
                });
            }
        };

        Bukkit.getPluginManager().registerEvent(
                trigger.getTarget(),
                EMPTY_LISTENER,
                EventPriority.NORMAL,
                executor,
                plugin);
    }
}
