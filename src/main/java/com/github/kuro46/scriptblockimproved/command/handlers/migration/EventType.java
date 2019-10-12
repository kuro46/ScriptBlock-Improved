package com.github.kuro46.scriptblockimproved.command.handlers.migration;

import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import lombok.Getter;
import lombok.NonNull;

enum EventType {
    INTERACT("sbinteract", "interact_Scripts.yml"),
    WALK("sbwalk", "walk_Scripts.yml");

    @Getter
    @NonNull
    private final TriggerName triggerName;
    @Getter
    @NonNull
    private final String fileName;

    private EventType(@NonNull final String triggerName, @NonNull final String fileName) {
        this.triggerName = TriggerName.of(triggerName);
        this.fileName = fileName;
    }
}
