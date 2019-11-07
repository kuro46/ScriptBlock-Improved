package com.github.kuro46.scriptblockimproved.script.option;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;

/**
 * Handler of option.
 */
public abstract class OptionHandler {

    @Getter
    @NonNull
    private final OptionName name;

    @Getter
    @NonNull
    private final Args args;

    public OptionHandler(@NonNull final OptionName name, @NonNull final Args args) {
        this.name = name;
        this.args = args;
    }

    public OptionHandler(@NonNull final String name, @NonNull final Args args) {
        this(OptionName.of(name), args);
    }

    public static Builder builder() {
        return new Builder();
    }

    public abstract PreExecuteResult preExecute(final ExecutionData data);

    public abstract void execute(final ExecutionData data);

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
        private OptionName name;

        public Builder name(final OptionName name) {
            this.name = name;
            return this;
        }

        public Builder name(final String name) {
            return name(OptionName.of(name));
        }

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
            Objects.requireNonNull(name, "'name' cannot be null");
            Objects.requireNonNull(preExecutor, "'preExecutor' cannot be null");
            Objects.requireNonNull(executor, "'executor' cannot be null");
            return new OptionHandler(name, args) {

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

        public void register(@NonNull final OptionHandlerMap handlers) {
            handlers.add(build());
        }
    }
}
