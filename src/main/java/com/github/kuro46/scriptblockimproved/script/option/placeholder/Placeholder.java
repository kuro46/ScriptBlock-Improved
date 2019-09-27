package com.github.kuro46.scriptblockimproved.script.option.placeholder;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import org.bukkit.entity.Player;

public interface Placeholder {

    PlaceholderName getName();

    String getReplaceTo(Player player, BlockPosition position);
}
