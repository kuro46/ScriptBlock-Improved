package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import org.bukkit.entity.Player;

public final class WorldPlaceholder implements Placeholder {

    @Override
    public PlaceholderName getName() {
        return PlaceholderName.of("world");
    }

    @Override
    public String getReplaceTo(final Player player, final BlockCoordinate coordinate) {
        return coordinate.getWorld();
    }
}
