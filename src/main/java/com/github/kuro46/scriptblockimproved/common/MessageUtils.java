package com.github.kuro46.scriptblockimproved.common;

import com.google.errorprone.annotations.FormatMethod;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @FormatMethod
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

    @FormatMethod
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

    public static String translateColorCodes(
            final char prefix,
            @NonNull final String target) {
        // prepare set
        final Set<Character> colorCodeSet = new HashSet<>();
        for (final char c : "1234567890abcdefklmnor".toCharArray()) colorCodeSet.add(c);
        // prepare set end
        final char[] chars = target.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == prefix && i + 1 < chars.length) {
                final char nextChar = Character.toLowerCase(chars[i + 1]);
                if (colorCodeSet.contains(nextChar)) {
                    chars[i] = '§';
                    // Force lowercased
                    chars[i + 1] = nextChar;
                }
            }
        }
        return new String(chars);
    }
}
