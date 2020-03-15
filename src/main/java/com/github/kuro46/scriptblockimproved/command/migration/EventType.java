package com.github.kuro46.scriptblockimproved.command.migration;

import lombok.Getter;
import lombok.NonNull;

enum EventType {
    INTERACT("sbinteract", "interact_Scripts.yml"),
    WALK("sbwalk", "walk_Scripts.yml");

    @Getter
    @NonNull
    private final String triggerName;
    @Getter
    @NonNull
    private final String fileName;

    private EventType(@NonNull final String triggerName, @NonNull final String fileName) {
        this.triggerName = triggerName;
        this.fileName = fileName;
    }
}
