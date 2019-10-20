package com.github.kuro46.scriptblockimproved.common;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;

public enum MessageKind {
    NORMAL(ChatColor.RESET),
    ERROR(ChatColor.RED),
    SUCCESS(ChatColor.GREEN);

    @Getter
    @NonNull
    private final ChatColor color;

    MessageKind(@NonNull final ChatColor color) {
        this.color = color;
    }
}
