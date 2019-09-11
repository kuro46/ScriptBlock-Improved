package com.github.kuro46.scriptblockimproved.command.clickaction;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import org.bukkit.entity.Player;

public interface Action {

    void action(Player player, BlockCoordinate coordinate);
}
