package com.github.kuro46.scriptblockimproved.common;

import java.util.Objects;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class MessageUtils {

    private static final String PREFIX = ChatColor.GRAY
        + "["
        + ChatColor.DARK_AQUA
        + "SB"
        + ChatColor.AQUA
        + "I"
        + ChatColor.GRAY
        + "] ";

    public static void sendMessage(
            final CommandSender sender,
            final String message) {
        sendMessage(sender, MessageKind.NORMAL, message);
    }

    public static void sendMessage(
            final CommandSender sender,
            final String message,
            final Object... args) {
        sendMessage(sender, MessageKind.NORMAL, String.format(message, args));
    }

    public static void sendMessage(
            final CommandSender sender,
            final MessageKind kind,
            final String message) {
        Objects.requireNonNull(sender, "'sender' cannot be null");
        Objects.requireNonNull(kind, "'kind' cannot be null");
        Objects.requireNonNull(message, "'message' cannot be null");

        sender.sendMessage(PREFIX + kind.getColor() + message);
    }

    public static void sendMessage(
            final CommandSender sender,
            final MessageKind kind,
            final String message,
            final Object... args) {
        sendMessage(sender, kind, String.format(message, args));
    }
}
