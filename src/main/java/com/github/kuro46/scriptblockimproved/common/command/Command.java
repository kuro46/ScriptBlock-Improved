package com.github.kuro46.scriptblockimproved.common.command;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public abstract class Command {

    private final Map<CommandName, Command> children = new LinkedHashMap<>();

    @Getter
    @NonNull
    private final CommandName name;
    @Getter
    @NonNull
    private final Args args;

    /**
     * A shorthand of<br>
     * <pre>{@code
     * new Command(CommandName.of(name), args);
     * }</pre>
     */
    public Command(@NonNull final String name, @NonNull final Args args) {
        this(CommandName.of(name), args);
    }

    public Command(@NonNull final CommandName name, @NonNull final Args args) {
        this.name = name;
        this.args = args;
    }

    public static Builder builder() {
        return new Builder();
    }

    public final void addChild(final Command command) {
        children.put(command.getName(), command);
    }

    public final Optional<Command> getChild(final CommandName section) {
        return Optional.ofNullable(children.get(section));
    }

    public final ImmutableMap<CommandName, Command> getChildren() {
        return ImmutableMap.copyOf(children);
    }

    public abstract void execute(ExecutionData data);

    public List<String> complete(final CompletionData data) {
        final List<String> children = data.getCommand().getChildren().keySet().stream()
            .map(CommandName::toString)
            .collect(Collectors.toList());
        return CandidateFactories.filter(CandidateFactories.created(children))
            .create(data.getCurrentValue());
    }

    public interface Executor {

        void execute(ExecutionData data);
    }

    public interface Completer {

        List<String> complete(CompletionData data);
    }

    public static class Builder {

        private final List<Command> children = new ArrayList<>();

        private CommandName name;
        private Args args;
        private Executor executor;
        private Completer completer;

        public Builder name(final String name) {
            this.name = CommandName.of(name);
            return this;
        }

        public Builder name(final CommandName name) {
            this.name = name;
            return this;
        }

        public Builder args(final Args args) {
            this.args = args;
            return this;
        }

        public Builder completer(final Completer completer) {
            this.completer = completer;
            return this;
        }

        public Builder executor(final Executor executor) {
            this.executor = executor;
            return this;
        }

        public Builder child(final Command command) {
            children.add(command);
            return this;
        }

        public Command build() {
            final Command command;
            if (completer == null) {
                command = new NoCompletableCommand(name, args, executor);
            } else {
                command = new CompletableCommand(name, args, executor, completer);
            }
            children.forEach(command::addChild);
            return command;
        }

        public void childOf(@NonNull final Command command) {
            command.addChild(build());
        }
    }

    private static class NoCompletableCommand extends Command {

        @NonNull
        private final Executor executor;

        public NoCompletableCommand(
            @NonNull final CommandName name,
            @NonNull final Args args,
            @NonNull final Executor executor
        ) {
            super(name, args);
            this.executor = executor;
        }

        @Override
        public void execute(@NonNull final ExecutionData data) {
            executor.execute(data);
        }
    }

    private static final class CompletableCommand extends NoCompletableCommand {

        @NonNull
        private final Completer completer;

        public CompletableCommand(
            @NonNull final CommandName name,
            @NonNull final Args args,
            @NonNull final Executor executor,
            @NonNull final Completer completer
        ) {
            super(name, args, executor);
            this.completer = completer;
        }

        @Override
        public List<String> complete(@NonNull final CompletionData data) {
            return completer.complete(data);
        }
    }
}
