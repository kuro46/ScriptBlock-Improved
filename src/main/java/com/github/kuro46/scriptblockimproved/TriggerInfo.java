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
public final class TriggerInfo {

    @Getter
    private final String name;
    private final Event event;

    public TriggerInfo(@NonNull final String name, Event event) {
        this.name = name;
        this.event = event;
    }

    public Optional<Event> getEvent() {
        return Optional.ofNullable(event);
    }
}
