package com.github.kuro46.scriptblockimproved.placeholder;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * This class is a representation of placeholder
 */
@ToString
public final class Placeholder {

    /**
     * Name of this placeholder
     */
    @Getter
    private final String name;

    /**
     * Target of this placeholder<br>
     * For exaplme, if name of this placeholder is "foo", target is "&lt;foo&gt;"
     */
    @Getter
    private final String target;

    /**
     * Factory of this placeholder
     */
    @Getter
    private final ReplacementFactory replacementFactory;

    public Placeholder(@NonNull String name, @NonNull ReplacementFactory factory) {
        this.name = name;
        this.replacementFactory = factory;
        this.target = "<" + name + ">";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private ReplacementFactory factory;

        public Builder name(final String name) {
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
