package com.github.kuro46.scriptblockimproved.script.trigger;

import org.bukkit.event.Event;

public interface Trigger {

    Class<? extends Event> getTarget();

    TriggerName getName();

    EventValidateResult validate(final Event event);
}
