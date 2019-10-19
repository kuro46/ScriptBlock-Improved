package com.github.kuro46.scriptblockimproved.script.trigger;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

public final class RegisteredTrigger {

    private final List<Runnable> onUnregisteredListeners = new ArrayList<>();

    @Getter
    private boolean unregistered = false;

    @Getter
    @NonNull
    private final TriggerName name;

    public RegisteredTrigger(@NonNull final TriggerName name) {
        this.name = name;
    }

    public void unregister() {
        unregistered = true;
        fireUnregistered();
    }

    public void onUnregistered(@NonNull final Runnable listener) {
        onUnregisteredListeners.add(listener);
    }

    private void fireUnregistered() {
        onUnregisteredListeners.forEach(Runnable::run);
    }
}
