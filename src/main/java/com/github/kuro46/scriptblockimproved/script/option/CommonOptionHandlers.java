package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.PermissionDetector;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

        handlers.add(OptionName.of("command"), COMMAND_HANDLER);
        handlers.add(OptionName.of("console"), CONSOLE_HANDLER);
        handlers.add(OptionName.of("broadcast"), BROADCAST_HANDLER);
        handlers.add(OptionName.of("say"), SAY_HANDLER);
        handlers.add(
                OptionName.of("bypassCommand"),
                new BypassCommandHandler(plugin));
    }

    private static final OptionHandler COMMAND_HANDLER = new OptionHandler() {

        private final Args args = Args.builder()
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
            final ParsedArgs args = data.getOption().getArgs();
            data.getPlayer().performCommand(removeSlashIfNeeded(args.getOrFail("command")));
        }
    };

    private static final OptionHandler CONSOLE_HANDLER = new OptionHandler() {

        private final Args args = Args.builder()
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
            final ParsedArgs args = data.getOption().getArgs();
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    removeSlashIfNeeded(args.getOrFail("command")));
        }
    };

    private static final OptionHandler BROADCAST_HANDLER = new OptionHandler() {

        private final Args args = Args.builder()
            .required("message")
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
            final ParsedArgs args = data.getOption().getArgs();
            Bukkit.broadcastMessage(args.getOrFail("message"));
        }
    };

    private static final OptionHandler SAY_HANDLER = new OptionHandler() {

        private final Args args = Args.builder()
            .required("message")
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
            final ParsedArgs args = data.getOption().getArgs();
            data.getPlayer().sendMessage(args.getOrFail("message"));
        }
    };

    private static String removeSlashIfNeeded(final String source) {
        if (source.isEmpty() || source.charAt(0) != '/') {
            return source;
        }
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
            final ParsedArgs args = data.getOption().getArgs();
            final Player player = data.getPlayer();
            final PermissionAttachment attachment = player.addAttachment(plugin);
            try {
                final String command = removeSlashIfNeeded(args.getOrFail("command"));
                final String argPerm = args.getOrFail("permission");

                final List<String> permissions;
                if (argPerm.equals("auto")) {
                    final List<String> detected = PermissionDetector.getInstance()
                        .getPermissionsByCommand(command).orElse(null);
                    if (detected == null) {
                        sendMessage(
                                player,
                                MessageKind.ERROR,
                                "No permissions found for '%s'",
                                command);
                        return;
                    }
                    permissions = detected;
                } else {
                    permissions = Collections.singletonList(argPerm);
                }

                permissions.forEach(permission -> {
                    attachment.setPermission(permission, true);
                });
                player.performCommand(command);
            } finally {
                attachment.remove();
            }
        }
    }
}
