package com.github.kuro46.scriptblockimproved.command.handler;

import com.github.kuro46.scriptblockimproved.command.syntax.Arguments;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.bukkit.command.CommandSender;

public final class CommandBuilder {

    private CommandSections sections;
    private Arguments syntax = new Arguments(Collections.emptyList());
    private BiConsumer<CommandSender, ExecutionArguments> executor;
    private BiFunction<CommandSender, CompletionArguments, List<String>> completer =
        (sender, args) -> Collections.emptyList();

    public CommandBuilder sections(final CommandSections sections) {
        this.sections = Objects.requireNonNull(sections, "'sections' cannot be null");
        return this;
    }

    public CommandBuilder syntax(final String syntax) {
        Objects.requireNonNull(syntax, "'syntax' cannot be null");

        this.syntax = Arguments.fromString(syntax)
            .orElseThrow(() -> new IllegalArgumentException("Illegal syntax"));
        return this;
    }

    public CommandBuilder sections(final String sections) {
        return sections(CommandSections.of(sections));
    }

    public CommandBuilder executor(final BiConsumer<CommandSender, ExecutionArguments> executor) {
        this.executor = Objects.requireNonNull(executor, "'executor' cannot be null");
        return this;
    }

    public CommandBuilder completer(
            final BiFunction<CommandSender, CompletionArguments, List<String>> completer) {
        this.completer = Objects.requireNonNull(completer, "'completer' cannot be null");
        return this;
    }

    public Command build() {
        Objects.requireNonNull(sections, "'sections' cannot be null");
        Objects.requireNonNull(executor, "'executor' cannot be null");
        Objects.requireNonNull(completer, "'completer' cannot be null");

        return new Command(sections, new CommandHandler() {

            @Override
            public Arguments getSyntax() {
                return syntax;
            }

            @Override
            public void execute(final CommandSender sender, final ExecutionArguments args) {
                executor.accept(sender, args);
            }

            @Override
            public List<String> complete(
                    final CommandSender sender,
                    final CompletionArguments args) {
                return completer.apply(sender, args);
            }
        });
    }

    public void register(final Commands commands) {
        commands.register(build());
    }
}
