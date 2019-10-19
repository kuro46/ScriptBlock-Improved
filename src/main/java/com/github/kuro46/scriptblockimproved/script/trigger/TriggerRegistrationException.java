package com.github.kuro46.scriptblockimproved.script.trigger;

import lombok.NonNull;

@SuppressWarnings("serial")
public final class TriggerRegistrationException extends RuntimeException {

    public TriggerRegistrationException(@NonNull final String message) {
        super(message);
    }
}
