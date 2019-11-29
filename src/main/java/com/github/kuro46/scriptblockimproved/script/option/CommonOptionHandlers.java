package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.PermissionDetector;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CommonOptionHandlers {

    public static void registerAll(
            @NonNull final Plugin plugin,
            @NonNull final OptionHandlerMap handlers) {
        OptionHandler.builder()
            .name("command")
            .args(Args.builder()
                .required("command")
                .build())
            .executor(data -> {
                final ParsedArgs args = data.getArgs();
                final String command = removeSlashIfNeeded(args.getOrFail("command"));
                data.getPlayer().performCommand(command);
            })
            .register(handlers);
        OptionHandler.builder()
            .name("console")
            .args(Args.builder()
                .required("command")
                .build())
            .executor(data -> {
                final ParsedArgs args = data.getArgs();
                final String command = removeSlashIfNeeded(args.getOrFail("command"));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            })
            .register(handlers);
        OptionHandler.builder()
            .name("broadcast")
            .args(Args.builder()
                .required("message")
                .build())
            .executor(data -> {
                final ParsedArgs args = data.getArgs();
                Bukkit.broadcastMessage(args.getOrFail("message"));
            })
            .register(handlers);
        OptionHandler.builder()
            .name("say")
            .args(Args.builder()
                .required("message")
                .build())
            .executor(data -> {
                final ParsedArgs args = data.getArgs();
                data.getPlayer().sendMessage(args.getOrFail("message"));
            })
            .register(handlers);
        OptionHandler.builder()
            .name("cancelEvent")
            .args(Args.empty())
            .onTriggered(event -> {
                if (!(event instanceof Cancellable)) {
                    throw new IllegalStateException(
                        event.getClass() + " does not implements Cancellable"
                    );
                }
                ((Cancellable) event).setCancelled(true);
            })
            .executor(data -> {
                // no-op
            })
            .register(handlers);
        OptionHandler.builder()
            .name("dontCancelEvent")
            .args(Args.empty())
            .onTriggered(event -> {
                if (!(event instanceof Cancellable)) {
                    throw new IllegalStateException(
                        event.getClass() + " does not implements Cancellable"
                    );
                }
                ((Cancellable) event).setCancelled(false);
            })
            .executor(data -> {
                // no-op
            })
            .register(handlers);
        handlers.add(new BypassCommandHandler(plugin));
    }

    private static String removeSlashIfNeeded(final String source) {
        if (source.isEmpty() || source.charAt(0) != '/') return source;
        // 'source' is not empty and starts with '/'
        return source.substring(1);
    }

    private static final class BypassCommandHandler extends OptionHandler {

        @NonNull
        private final Plugin plugin;

        public BypassCommandHandler(@NonNull final Plugin plugin) {
            super(
                "bypassCommand",
                Args.builder()
                    .requiredArgs("permission", "command")
                    .build()
            );
            this.plugin = plugin;
        }

        @Override
        public void onTriggered(final Event event) {
            // no-op
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
