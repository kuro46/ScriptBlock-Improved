package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import org.bukkit.entity.Player;

public final class WorldPlaceholder implements Placeholder {

    @Override
    public PlaceholderName getName() {
        return PlaceholderName.of("world");
    }

    @Override
    public String getReplaceTo(final Player player, final BlockPosition position) {
        return position.getWorld();
    }
}
