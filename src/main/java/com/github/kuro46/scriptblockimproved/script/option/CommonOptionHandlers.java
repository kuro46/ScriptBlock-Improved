package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.commandutility.syntax.CommandSyntax;
import com.github.kuro46.commandutility.syntax.CommandSyntaxBuilder;
import com.github.kuro46.commandutility.syntax.LongArgument;
import com.github.kuro46.commandutility.syntax.RequiredArgument;
import com.github.kuro46.scriptblockimproved.script.Script;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

public final class CommonOptionHandlers {

    public static void registerAll(final Plugin plugin, final OptionHandlers handlers) {
        Objects.requireNonNull(handlers, "'handlers' cannot be null");
        Objects.requireNonNull(plugin, "'plugin' cannot be null");

        handlers.add(OptionName.of("command"), COMMAND_HANDLER);
        handlers.add(OptionName.of("console"), CONSOLE_HANDLER);
        handlers.add(OptionName.of("broadcast"), BROADCAST_HANDLER);
        handlers.add(OptionName.of("say"), SAY_HANDLER);
        handlers.add(OptionName.of("bypassCommand"), new BypassCommandHandler(plugin));
        handlers.add(OptionName.of("cancelEvent"), EVENT_CANCEL_HANDLER);
    }

    private static final OptionHandler EVENT_CANCEL_HANDLER = new OptionHandler() {
        @Override
        public CommandSyntax getSyntax() {
            return new CommandSyntaxBuilder()
                .addArgument(new LongArgument("", false))
                .build();
        }

        @Override
        public ValidateResult validate(final Arguments args) {
            return ValidateResult.VALID;
        }

        @Override
        public Arguments normalize(final Arguments rawArgs) {
            return rawArgs;
        }

        @Override
        public CheckResult check(
            final Event event,
            final Player player,
            final Script script,
            final OptionName name,
            final Arguments args) {
            return CheckResult.CONTINUE;
        }

        @Override
        public void execute(
            final Event event,
            final Player player,
            final Script script,
            final OptionName name,
            final Arguments args) {
            ((Cancellable) event).setCancelled(true);
        }
    };

    private static final OptionHandler COMMAND_HANDLER = new OptionHandler() {

        private final CommandSyntax syntax = new CommandSyntaxBuilder()
            .addArgument(new LongArgument("command", true))
            .build();

        @Override
        public CommandSyntax getSyntax() {
            return syntax;
        }

        @Override
        public ValidateResult validate(final Arguments args) {
            return ValidateResult.VALID;
        }

        @Override
        public Arguments normalize(final Arguments args) {
            final Map<String, String> mutable = args.toMutable();
            mutable.compute("command", (key, value) -> removeSlashIfNeeded(value));
            return new Arguments(mutable);
        }

        @Override
        public CheckResult check(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            return CheckResult.CONTINUE;
        }

        @Override
        public void execute(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            player.performCommand(args.getOrFail("command"));
        }
    };

    private static final OptionHandler CONSOLE_HANDLER = new OptionHandler() {

        private final CommandSyntax syntax = new CommandSyntaxBuilder()
            .addArgument(new LongArgument("command", true))
            .build();

        @Override
        public CommandSyntax getSyntax() {
            return syntax;
        }

        @Override
        public ValidateResult validate(final Arguments args) {
            return ValidateResult.VALID;
        }

        @Override
        public Arguments normalize(final Arguments args) {
            final Map<String, String> mutable = args.toMutable();
            mutable.compute("command", (key, value) -> removeSlashIfNeeded(value));
            return new Arguments(mutable);
        }

        @Override
        public CheckResult check(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            return CheckResult.CONTINUE;
        }

        @Override
        public void execute(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    args.getOrFail("command"));
        }
    };

    private static final OptionHandler BROADCAST_HANDLER = new OptionHandler() {

        private final CommandSyntax syntax = new CommandSyntaxBuilder()
            .addArgument(new LongArgument("message", true))
            .build();

        @Override
        public CommandSyntax getSyntax() {
            return syntax;
        }

        @Override
        public ValidateResult validate(final Arguments args) {
            return ValidateResult.VALID;
        }

        @Override
        public Arguments normalize(final Arguments args) {
            return args;
        }

        @Override
        public CheckResult check(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            return CheckResult.CONTINUE;
        }

        @Override
        public void execute(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            Bukkit.broadcastMessage(args.getOrFail("message"));
        }
    };

    private static final OptionHandler SAY_HANDLER = new OptionHandler() {

        private final CommandSyntax syntax = new CommandSyntaxBuilder()
            .addArgument(new LongArgument("message", true))
            .build();

        @Override
        public CommandSyntax getSyntax() {
            return syntax;
        }

        @Override
        public ValidateResult validate(final Arguments args) {
            return ValidateResult.VALID;
        }

        @Override
        public Arguments normalize(final Arguments args) {
            return args;
        }

        @Override
        public CheckResult check(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            return CheckResult.CONTINUE;
        }

        @Override
        public void execute(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            player.sendMessage(args.getOrFail("message"));
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
            this.plugin = plugin;
        }

        private final CommandSyntax syntax = new CommandSyntaxBuilder()
            .addArgument(new RequiredArgument("permission"))
            .addArgument(new LongArgument("command", true))
            .build();

        @Override
        public CommandSyntax getSyntax() {
            return syntax;
        }

        @Override
        public ValidateResult validate(final Arguments args) {
            return ValidateResult.VALID;
        }

        @Override
        public Arguments normalize(final Arguments args) {
            final Map<String, String> mutable = args.toMutable();
            mutable.compute("command", (key, value) -> removeSlashIfNeeded(value));
            return new Arguments(mutable);
        }

        @Override
        public CheckResult check(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            return CheckResult.CONTINUE;
        }

        @Override
        public void execute(
                final Event event,
                final Player player,
                final Script script,
                final OptionName name,
                final Arguments args) {
            final PermissionAttachment attachment = player.addAttachment(plugin);
            try {
                attachment.setPermission(args.getOrFail("permission"), true);
                player.performCommand(args.getOrFail("command"));
            } finally {
                attachment.remove();
            }
        }
    }
}
