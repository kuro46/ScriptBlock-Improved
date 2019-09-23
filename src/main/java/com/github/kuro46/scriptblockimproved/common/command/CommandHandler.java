package com.github.kuro46.scriptblockimproved.common.command;

import java.util.Objects;
import org.bukkit.command.CommandSender;

public abstract class CommandHandler {

    private final Args args;

    public CommandHandler(final Args args) {
        this.args = Objects.requireNonNull(args, "'args' cannot be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Args getArgs() {
        return args;
    }

    public abstract void execute(CommandManager manager, CommandSender sender, ParsedArgs args);

    @FunctionalInterface
    public interface Executor {

        void execute(CommandManager manager, CommandSender sender, ParsedArgs args);
    }

    public static class Builder {

        private Args args;
        private Executor executor;

        public Builder args(final Args args) {
            this.args = args;

            return this;
        }

        public Builder executor(final Executor executor) {
            this.executor = executor;

            return this;
        }

        public CommandHandler build() {
            return new CommandHandler(args) {

                @Override
                public void execute(
                        final CommandManager manager,
                        final CommandSender sender,
                        final ParsedArgs args) {
                    executor.execute(manager, sender, args);
                }
            };
        }
    }
}
