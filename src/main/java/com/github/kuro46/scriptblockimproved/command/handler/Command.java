package com.github.kuro46.scriptblockimproved.command.handler;

import java.util.Objects;

public final class Command {

    private final CommandSections sections;
    private final CommandHandler handler;

    public Command(final CommandSections sections, final CommandHandler handler) {
        this.sections = Objects.requireNonNull(sections, "'sections' cannot be null");
        this.handler = Objects.requireNonNull(handler, "'handler' cannot be null");
    }

    public static CommandBuilder builder() {
        return new CommandBuilder();
    }

    public CommandSections getSections() {
        return sections;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    public void register(final Commands commands) {
        commands.register(this);
    }
}
