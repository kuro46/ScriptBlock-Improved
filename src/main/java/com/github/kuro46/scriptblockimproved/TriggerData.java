package com.github.kuro46.scriptblockimproved;

import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.event.Event;

@EqualsAndHashCode
@ToString
@Builder
public final class TriggerData {

    @Getter
    private final Trigger trigger;
    private final boolean shouldSuppress;
    private final Event event;

    public TriggerData(@NonNull final Trigger trigger, final boolean shouldSuppress, Event event) {
        this.trigger = trigger;
        this.shouldSuppress = shouldSuppress;
        this.event = event;
    }

    public boolean shouldSuppress() {
        return shouldSuppress;
    }

    public Optional<Event> getEvent() {
        return Optional.ofNullable(event);
    }
}
