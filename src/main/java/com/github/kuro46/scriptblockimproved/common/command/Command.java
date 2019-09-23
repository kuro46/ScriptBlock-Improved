package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class Command {

    private final Map<CommandSection, Command> children = new LinkedHashMap<>();

    private final CommandSection section;
    private final String description;
    private final CommandHandler handler;

    public Command(
            final CommandSection section,
            final CommandHandler handler) {
        this(section, handler, null);
    }

    public Command(
            final CommandSection section,
            final CommandHandler handler,
            final String description) {
        this.section = Objects.requireNonNull(section, "'section' cannot be null");
        this.handler = Objects.requireNonNull(handler, "'handler' cannot be null");
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public CommandSection getSection() {
        return section;
    }

    public String getDescription() {
        return description;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    public void addChild(final Command command) {
        children.put(command.getSection(), command);
    }

    public Optional<Command> getChild(final CommandSection section) {
        return Optional.ofNullable(children.get(section));
    }

    public ImmutableMap<CommandSection, Command> getChildren() {
        return ImmutableMap.copyOf(children);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("children", children)
            .add("section", section)
            .add("handler", handler)
            .add("description", description)
            .toString();
    }

    public static class Builder {

        private final List<Command> children = new ArrayList<>();

        private CommandSection section;
        private String description;
        private CommandHandler handler;

        public Builder section(final CommandSection section) {
            this.section = section;

            return this;
        }

        public Builder section(final String section) {
            this.section = CommandSection.of(section);

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

        public Builder child(final Command command) {
            children.add(command);

            return this;
        }

        public Command build() {
            final Command command = new Command(section, handler, description);
            children.forEach(child -> command.addChild(child));
            return command;
        }

        public void register(final CommandManager manager) {
            manager.registerCommand(build());
        }

        public void childOf(final Command command) {
            Objects.requireNonNull(command, "'command' cannot be null");

            command.addChild(build());
        }
    }
}
