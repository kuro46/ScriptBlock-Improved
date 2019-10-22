package com.github.kuro46.scriptblockimproved.script.trigger;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.apache.commons.lang3.mutable.MutableInt;

public final class RegisteredTriggerGroup {

    private final List<UnregisteredListener> listenerList = new ArrayList<>();
    private final MutableInt unregisteredCount = new MutableInt();

    private final ImmutableList<RegisteredTrigger> list;

    public RegisteredTriggerGroup(@NonNull final List<RegisteredTrigger> list) {
        this.list = ImmutableList.copyOf(list);
        this.list.forEach(listener -> listener.onUnregistered(this::incrementUnregisteredCount));
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Add a listener that called when all triggers are unregistered.
     */
    public void onUnregisteredAll(@NonNull final UnregisteredListener listener) {
        listenerList.add(listener);
    }

    private void incrementUnregisteredCount() {
        unregisteredCount.increment();
        if (isFirable()) fireUnregistered();
    }

    private boolean isFirable() {
        return list.size() == unregisteredCount.intValue();
    }

    private void fireUnregistered() {
        listenerList.forEach(UnregisteredListener::onUnregistered);
    }

    public static final class Builder {

        private final List<RegisteredTrigger> list = new ArrayList<>();

        public Builder add(@NonNull final RegisteredTrigger trigger) {
            list.add(trigger);
            return this;
        }

        public RegisteredTriggerGroup build() {
            return new RegisteredTriggerGroup(list);
        }
    }
}
