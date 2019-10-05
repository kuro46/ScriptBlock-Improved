package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;

public final class PlaceholderGroup {

    private final Map<PlaceholderName, Placeholder> placeholders = new HashMap<>();

    public void add(final Placeholder placeholder) {
        placeholders.put(placeholder.getName(), placeholder);
    }

    public String replace(@NonNull String source, @NonNull final SourceData data) {
        for (final Placeholder placeholder : placeholders.values()) {
            source = placeholder.replace(source, data);
        }
        return source;
    }
}
