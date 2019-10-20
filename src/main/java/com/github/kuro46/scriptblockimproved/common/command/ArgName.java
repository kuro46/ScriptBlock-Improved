package com.github.kuro46.scriptblockimproved.common.command;

import java.util.Formattable;
import java.util.Formatter;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * Name of an argument.
 * <p>
 * <pre>{@code
 * ArgName name1 = ArgName.of("Argument Name");
 * ArgName name2 = ArgName.of("argument name");
 * assertEquals(name1, name2);
 * }</pre>
 */
@EqualsAndHashCode
@ToString
public final class ArgName implements Formattable {

    @NonNull
    private final String name;

    private ArgName(@NonNull final String name) {
        this.name = name.toLowerCase();
    }

    public static ArgName of(@NonNull final String name) {
        return new ArgName(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public void formatTo(
            final Formatter formatter,
            final int flags,
            final int width,
            final int precision) {
        formatter.format("%s", name);
    }
}
