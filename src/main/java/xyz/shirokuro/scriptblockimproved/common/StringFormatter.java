package xyz.shirokuro.scriptblockimproved.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StringFormatter {

    private final List<Object> args = new ArrayList<>();
    private final String format;

    public StringFormatter(final String format) {
        this.format = Objects.requireNonNull(format);
    }

    public StringFormatter argument(final Object argument) {
        args.add(argument);
        return this;
    }

    public String build() {
        return String.format(format, args.toArray());
    }
}
