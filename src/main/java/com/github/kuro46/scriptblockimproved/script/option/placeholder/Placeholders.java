package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public final class Placeholders {

    private final Map<PlaceholderName, Placeholder> placeholders = new HashMap<>();

    public void add(final Placeholder placeholder) {
        placeholders.put(placeholder.getName(), placeholder);
    }

    // TODO: Improve performance
    public String replace(
            String source,
            final Player player,
            final BlockCoordinate coordinate) {
        for (final Placeholder placeholder : placeholders.values()) {
            source = source.replace(
                    "<" + placeholder.getName() + ">",
                    placeholder.getReplaceTo(player, coordinate));
        }
        return source;
    }
}
