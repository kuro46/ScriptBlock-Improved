package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import java.util.Objects;
import lombok.NonNull;

public interface OptionHandler {

    Args getArgs();

    PreExecuteResult preExecute(final ExecutionData data);

    void execute(final ExecutionData data);

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Functional interface for Builder
     */
    @FunctionalInterface
    public interface PreExecutor {

        PreExecuteResult execute(final ExecutionData data);
    }

    /**
     * Functional interface for Builder
     */
    @FunctionalInterface
    public interface Executor {

        void execute(final ExecutionData data);
    }

    public static final class Builder {

        private PreExecutor preExecutor = data -> PreExecuteResult.CONTINUE;
        private Executor executor;
        private Args args;

        public Builder args(final Args args) {
            this.args = args;
            return this;
        }

        public Builder preExecutor(final PreExecutor preExecutor) {
            this.preExecutor = preExecutor;
            return this;
        }

        public Builder executor(final Executor executor) {
            this.executor = executor;
            return this;
        }

        public OptionHandler build() {
            Objects.requireNonNull(args, "'args' cannot be null");
            Objects.requireNonNull(preExecutor, "'preExecutor' cannot be null");
            Objects.requireNonNull(executor, "'executor' cannot be null");
            return new OptionHandler() {

                @Override
                public Args getArgs() {
                    return args;
                }

                @Override
                public PreExecuteResult preExecute(final ExecutionData data) {
                    return preExecutor.execute(data);
                }

                @Override
                public void execute(final ExecutionData data) {
                    executor.execute(data);
                }
            };
        }

        public void register(
                @NonNull final OptionHandlerMap handlers,
                @NonNull final OptionName name) {
            handlers.add(name, build());
        }

        public void register(
                @NonNull final OptionHandlerMap handlers,
                @NonNull final String name) {
            register(handlers, OptionName.of(name));
        }
    }
}
