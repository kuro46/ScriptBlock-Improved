package com.github.kuro46.scriptblockimproved.common;

import java.util.List;
import lombok.NonNull;
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
            @NonNull final CommandSender sender,
            @NonNull final MessageKind kind,
            @NonNull final String message) {
        sender.sendMessage(PREFIX + kind.getColor() + message);
    }

    public static void sendMessage(
            final CommandSender sender,
            final MessageKind kind,
            final String message,
            final Object... args) {
        sendMessage(sender, kind, String.format(message, args));
    }

    public static void sendMessage(
            final CommandSender sender,
            final List<String> messages) {
        messages.forEach(message -> sendMessage(sender, message));
    }
}
