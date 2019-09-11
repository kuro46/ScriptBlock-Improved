package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import org.bukkit.entity.Player;

public interface Placeholder {

    PlaceholderName getName();

    String getReplaceTo(Player player, BlockCoordinate coordinate);
}
