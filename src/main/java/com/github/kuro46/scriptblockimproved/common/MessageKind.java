package com.github.kuro46.scriptblockimproved.common;

import java.util.Objects;
import org.bukkit.ChatColor;

public enum MessageKind {
    NORMAL(ChatColor.RESET),
    ERROR(ChatColor.RED),
    SUCCESS(ChatColor.GREEN);

    private final ChatColor color;

    MessageKind(final ChatColor color) {
        this.color = Objects.requireNonNull(color, "'color' cannot be null");;
    }

    public ChatColor getColor() {
        return color;
    }
}
