package com.github.kuro46.scriptblockimproved.common.command;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public abstract class CommandHandler {

    @Getter
    @NonNull
    private final Args args;

    public CommandHandler(@NonNull final Args args) {
        this.args = args;
    }

    public static Builder builder() {
        return new Builder();
    }

    public abstract void execute(ExecutionData data);

    public List<String> complete(final CompletionData data) {
        final List<String> children = data.getCommand().getChildren().keySet().stream()
            .map(CommandSection::getName)
            .collect(Collectors.toList());
        return CandidateFactories.filter(CandidateFactories.created(children))
            .create(data.getCurrentValue());
    }

    @FunctionalInterface
    public interface Executor {

        void execute(ExecutionData data);
    }

    @FunctionalInterface
    public interface Completer {

        List<String> complete(CompletionData data);
    }

    public static class Builder {

        private Args args;
        private Executor executor;
        private Completer completer;

        public Builder args(final Args args) {
            this.args = args;

            return this;
        }

        public Builder executor(final Executor executor) {
            this.executor = executor;

            return this;
        }

        public Builder completer(final Completer completer) {
            this.completer = completer;

            return this;
        }

        public CommandHandler build() {
            return new CommandHandler(args) {

                @Override
                public void execute(final ExecutionData data) {
                    executor.execute(data);
                }

                @Override
                public List<String> complete(final CompletionData data) {
                    return completer.complete(data);
                }
            };
        }
    }
}
