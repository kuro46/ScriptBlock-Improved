package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * <pre>{@code
 * Command.builder()
 *     .name("foo bar")
 *     .description("Description of '/foo bar'")
 *     .handler(CommandHandler.builder()
 *         .args(Args.builder()
 *             .required("arg1")
 *             .optional("arg2")
 *             .build())
 *         .executor((sender, args) -> {
 *             sender.sendMessage(String.format("arg1: %s arg2: %s", arg1, arg2));
 *         })
 *         .build();
 *     .register(manager);
 * }</pre>
 */
public final class Command {

    private final CommandName name;
    private final String description;
    private final CommandHandler handler;

    public Command(
            final CommandName name,
            final CommandHandler handler) {
        this(name, handler, null);
    }

    public Command(
            final CommandName name,
            final CommandHandler handler,
            final String description) {
        this.name = Objects.requireNonNull(name, "'name' cannot be null");
        this.handler = Objects.requireNonNull(handler, "'handler' cannot be null");
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CommandName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("handler", handler)
            .add("description", description)
            .toString();
    }

    public static class Builder {

        private CommandName name;
        private String description;
        private CommandHandler handler;

        public Builder name(final CommandName name) {
            this.name = name;

            return this;
        }

        public Builder name(final String name) {
            this.name = CommandName.of(name);

            return this;
        }

        public Builder description(final String description) {
            this.description = description;

            return this;
        }

        public Builder handler(final CommandHandler handler) {
            this.handler = handler;

            return this;
        }

        public Command build() {
            return new Command(name, handler, description);
        }

        public void register(final CommandManager manager) {
            manager.registerCommand(build());
        }
    }
}
