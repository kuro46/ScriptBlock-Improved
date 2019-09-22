package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.bukkit.command.CommandSender;

/**
 * <pre>{@code
 * Command.builder()
 *     .name("foo bar")
 *     .description("Description of '/foo bar'")
 *     .args(Args.builder()
 *         .required("arg1")
 *         .optional("arg2")
 *         .build())
 *     .executor((sender, args) -> {
 *         sender.sendMessage(String.format("arg1: %s arg2: %s", arg1, arg2));
 *     })
 *     .register(manager);
 * }</pre>
 */
public final class Command {

    private final CommandName name;
    private final Args args;
    private final Executor executor;
    private final String description;

    public Command(
            final CommandName name,
            final Args args,
            final Executor executor) {
        this(name, args, executor, null);
    }

    public Command(
            final CommandName name,
            final Args args,
            final Executor executor,
            final String description) {
        this.name = Objects.requireNonNull(name, "'name' cannot be null");
        this.args = Objects.requireNonNull(args, "'args' cannot be null");
        this.executor = Objects.requireNonNull(executor, "'executor' cannot be null");
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CommandName getName() {
        return name;
    }

    public Args getArgs() {
        return args;
    }

    public Executor getExecutor() {
        return executor;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("args", args)
            .add("executor", executor)
            .add("description", description)
            .toString();
    }

    @FunctionalInterface
    public interface Executor {

        void execute(CommandSender sender, ParsedArgs args);
    }

    public static class Builder {

        private CommandName name;
        private Args args = Args.empty();
        private Executor executor;
        private String description;

        public Builder name(final CommandName name) {
            this.name = name;
            return this;
        }

        public Builder name(final String name) {
            this.name = CommandName.of(name);
            return this;
        }

        public Builder args(final Args args) {
            this.args = args;
            return this;
        }

        public Builder executor(final Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Command build() {
            return new Command(name, args, executor, description);
        }

        public void register(final CommandManager manager) {
            manager.registerCommand(build());
        }
    }
}
