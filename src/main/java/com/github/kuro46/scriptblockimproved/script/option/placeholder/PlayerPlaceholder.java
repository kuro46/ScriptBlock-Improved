package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import org.bukkit.entity.Player;

public final class PlayerPlaceholder implements Placeholder {

    @Override
    public PlaceholderName getName() {
        return PlaceholderName.of("player");
    }

    @Override
    public String getReplaceTo(final Player player, final BlockCoordinate coordinate) {
        return player.getName();
    }
}
