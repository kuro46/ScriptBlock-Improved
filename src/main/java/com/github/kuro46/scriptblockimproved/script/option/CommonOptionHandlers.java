package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.PermissionDetector;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CommonOptionHandlers {

    public static void registerAll(
            final Plugin plugin,
            final OptionHandlers handlers) {
        Objects.requireNonNull(handlers, "'handlers' cannot be null");
        Objects.requireNonNull(plugin, "'plugin' cannot be null");

        OptionHandler.builder()
            .args(Args.builder()
                .required("command")
                .build())
            .executor(data -> {
                final ParsedArgs args = data.getArgs();
                final String command = removeSlashIfNeeded(args.getOrFail("command"));
                data.getPlayer().performCommand(command);
            })
            .register(handlers, "command");
        OptionHandler.builder()
            .args(Args.builder()
                .required("command")
                .build())
            .executor(data -> {
                final ParsedArgs args = data.getArgs();
                final String command = removeSlashIfNeeded(args.getOrFail("command"));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            })
            .register(handlers, "console");
        OptionHandler.builder()
            .args(Args.builder()
                .required("message")
                .build())
            .executor(data -> {
                final ParsedArgs args = data.getArgs();
                Bukkit.broadcastMessage(args.getOrFail("message"));
            })
            .register(handlers, "broadcast");
        OptionHandler.builder()
            .args(Args.builder()
                .required("message")
                .build())
            .executor(data -> {
                final ParsedArgs args = data.getArgs();
                data.getPlayer().sendMessage(args.getOrFail("message"));
            })
            .register(handlers, "say");
        handlers.add("bypassCommand", new BypassCommandHandler(plugin));
    }

    private static String removeSlashIfNeeded(final String source) {
        if (source.isEmpty() || source.charAt(0) != '/') return source;
        // 'source' is not empty and starts with '/'
        return source.substring(1);
    }

    private static final class BypassCommandHandler implements OptionHandler {

        private final Plugin plugin;

        public BypassCommandHandler(final Plugin plugin) {
            this.plugin = Objects.requireNonNull(plugin, "'plugin' cannot be null");;
        }

        private final Args args = Args.builder()
            .required("permission")
            .required("command")
            .build();

        @Override
        public Args getArgs() {
            return args;
        }

        @Override
        public PreExecuteResult preExecute(final ExecutionData data) {
            return PreExecuteResult.CONTINUE;
        }

        @Override
        public void execute(final ExecutionData data) {
            final ParsedArgs args = data.getArgs();
            final Player player = data.getPlayer();
            final PermissionAttachment attachment = player.addAttachment(plugin);
            try {
                final String command = removeSlashIfNeeded(args.getOrFail("command"));
                final String maybePerm = args.getOrFail("permission");
                final List<String> permissions =
                    getPermsByStr(player, command, maybePerm).orElse(null);
                if (permissions == null) return;
                permissions.forEach(permission -> attachment.setPermission(permission, true));
                player.performCommand(command);
            } finally {
                attachment.remove();
            }
        }

        private Optional<List<String>> getPermsByStr(
                @NonNull final Player player,
                @NonNull final String command,
                @NonNull final String str) {
            if (!str.equalsIgnoreCase("auto")) return Optional.of(Collections.singletonList(str));
            final List<String> detected = PermissionDetector.getInstance()
                .getPermissionsByCommand(command).orElse(null);
            if (detected == null) {
                sendMessage(
                        player,
                        MessageKind.ERROR,
                        "No permissions found for '%s'",
                        command);
                return Optional.empty();
            }
            return Optional.of(detected);
        }
    }
}
