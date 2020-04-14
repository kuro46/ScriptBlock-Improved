package xyz.shirokuro.scriptblockimproved.handler;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.TriggerData;
import xyz.shirokuro.scriptblockimproved.common.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BypassCommandHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerData triggerData, Player player, List<String> args) {
        final PermissionAttachment attachment = player.addAttachment(ScriptBlockImproved.getInstance().getPlugin());
        try {
            final String maybePerm = args.get(0);
            final String command = Utils.removeSlashIfNeeded(String.join(" ", args.subList(1, args.size())));
            final Set<String> permissions = getPermsByStr(player, command, maybePerm).orElse(null);
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

    private Optional<Set<String>> getPermsByStr(
        @NonNull final Player player,
        @NonNull final String command,
        @NonNull final String str) {
        if (!str.equalsIgnoreCase("auto")) return Optional.of(Collections.singleton(str));
        final Set<String> detected = ScriptBlockImproved.getInstance().getPermissionDetector()
            .getPermissionsByCommand(command);
        if (detected.isEmpty()) {
            player.sendMessage(String.format("No permissions found for '%s'", command));
            return Optional.empty();
        }
        return Optional.of(detected);
    }
}
