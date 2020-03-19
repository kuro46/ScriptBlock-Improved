package xyz.shirokuro.scriptblockimproved.placeholder;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.apache.commons.text.TextStringBuilder;

/**
 * Group of Placeholder.
 */
public final class PlaceholderGroup {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("<(.+?)>");

    private final Map<String, Placeholder> placeholders = new HashMap<>();

    public void add(final Placeholder placeholder) {
        placeholders.put(placeholder.getName(), placeholder);
    }

    public String replace(@NonNull final String source, @NonNull final SourceData data) {
        final ImmutableList<String> holderNames = findPlaceholders(source);
        if (holderNames.isEmpty()) return source;
        final TextStringBuilder builder = new TextStringBuilder(source);
        for (final String holderName : holderNames) {
            final Placeholder holder = placeholders.get(holderName);
            if (holder == null) continue;
            builder.replaceAll(holder.getTarget(), holder.getReplacementFactory().create(data));
        }
        return builder.build();
    }

    private ImmutableList<String> findPlaceholders(@NonNull String source) {
        final ImmutableList.Builder<String> placeholders = ImmutableList.builder();
        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(source);
        while (matcher.find()) placeholders.add(matcher.group(1));
        return placeholders.build();
    }
}
