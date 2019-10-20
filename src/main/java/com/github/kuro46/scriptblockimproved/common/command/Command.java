package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public final class Command {

    private final Map<CommandSection, Command> children = new LinkedHashMap<>();

    @Getter
    @NonNull
    private final CommandSection section;
    @Getter
    @NonNull
    private final CommandHandler handler;

    public Command(
            @NonNull final CommandSection section,
            @NonNull final CommandHandler handler) {
        this.section = section;
        this.handler = handler;
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {

        private final List<Command> children = new ArrayList<>();

        private CommandSection section;
        private CommandHandler handler;

        public Builder section(final CommandSection section) {
            this.section = section;

            return this;
        }

        public Builder section(final String section) {
            this.section = CommandSection.of(section);

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
            final Command command = new Command(section, handler);
            children.forEach(command::addChild);
            return command;
        }

        public void childOf(@NonNull final Command command) {
            command.addChild(build());
        }
    }
}
