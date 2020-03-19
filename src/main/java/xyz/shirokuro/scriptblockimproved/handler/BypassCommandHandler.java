package xyz.shirokuro.scriptblockimproved.handler;

import xyz.shirokuro.scriptblockimproved.PermissionDetector;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.TriggerData;
import xyz.shirokuro.scriptblockimproved.common.Utils;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class BypassCommandHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerData triggerData, Player player, ImmutableList<String> args) {
        final PermissionAttachment attachment = player.addAttachment(ScriptBlockImproved.getInstance().getPlugin());
        try {
            final String maybePerm = args.get(0);
            final String command = Utils.removeSlashIfNeeded(String.join(" ", args.subList(1, args.size())));
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
