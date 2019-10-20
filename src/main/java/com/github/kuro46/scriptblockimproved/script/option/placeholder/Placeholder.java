package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
public final class Placeholder {

    /**
     * Name of this placeholder
     */
    @NonNull
    @Getter
    private final PlaceholderName name;

    /**
     * Target of this placeholder<br>
     * For exaplme, if name of this placeholder is "foo", target is "&lt;foo&gt;"
     */
    @NonNull
    @Getter
    private final String target;

    /**
     * Factory of this placeholder
     */
    @NonNull
    @Getter
    private final ReplacementFactory replacementFactory;

    public Placeholder(@NonNull final String name, @NonNull ReplacementFactory factory) {
        this(PlaceholderName.of(name), factory);
    }

    public Placeholder(@NonNull PlaceholderName name, @NonNull ReplacementFactory factory) {
        this.name = name;
        this.replacementFactory = factory;
        this.target = "<" + name.getName() + ">";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private PlaceholderName name;
        private ReplacementFactory factory;

        public Builder name(final String name) {
            return name(PlaceholderName.of(name));
        }

        public Builder name(final PlaceholderName name) {
            this.name = name;
            return this;
        }

        public Builder factory(final ReplacementFactory factory) {
            this.factory = factory;
            return this;
        }

        public Placeholder build() {
            return new Placeholder(name, factory);
        }
    }
}
