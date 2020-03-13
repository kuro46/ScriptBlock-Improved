package com.github.kuro46.scriptblockimproved.handler;

import com.github.kuro46.scriptblockimproved.PermissionDetector;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.TriggerInfo;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BypassCommandHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerInfo triggerInfo, Player player, ImmutableList<String> args) {
        final PermissionAttachment attachment = player.addAttachment(ScriptBlockImproved.getInstance().getPlugin());
        try {
            final String maybePerm = args.get(0);
            final String command = ScriptBlockImproved.removeSlashIfNeeded(String.join(" ", args.subList(1, args.size())));
            final List<String> permissions = getPermsByStr(player, command, maybePerm).orElse(null);
            if (permissions == null) return;
            permissions.forEach(permission -> attachment.setPermission(permission, true));
            player.performCommand(command);
        } finally {
            attachment.remove();
        }
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return args.size() > 1 ? ValidationResult.VALID : ValidationResult.INVALID;
    }

    private Optional<List<String>> getPermsByStr(
        @NonNull final Player player,
        @NonNull final String command,
        @NonNull final String str) {
        if (!str.equalsIgnoreCase("auto")) return Optional.of(Collections.singletonList(str));
        final List<String> detected = PermissionDetector.getInstance()
            .getPermissionsByCommand(command).orElse(null);
        if (detected == null) {
            player.sendMessage(String.format("No permissions found for '%s'", command));
            return Optional.empty();
        }
        return Optional.of(detected);
    }
}
