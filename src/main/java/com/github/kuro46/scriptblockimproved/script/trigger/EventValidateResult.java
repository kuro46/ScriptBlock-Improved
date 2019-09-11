package com.github.kuro46.scriptblockimproved.script.trigger;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public final class EventValidateResult {

    private static final EventValidateResult RESULT_INVALID = new EventValidateResult(null);

    private final EventData eventData;

    private EventValidateResult(final EventData eventData) {
        this.eventData = eventData;
    }

    public static EventValidateResult valid(final EventData eventData) {
        Objects.requireNonNull(eventData, "'eventData' cannot be null");

        return new EventValidateResult(eventData);
    }

    public static EventValidateResult invalid() {
        return RESULT_INVALID;
    }

    public boolean isValid() {
        return eventData != null;
    }

    public boolean isInvalid() {
        return eventData == null;
    }

    public EventData getEventData() {
        return Objects.requireNonNull(eventData);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("eventData", eventData)
            .toString();
    }
}
