package com.github.kuro46.scriptblockimproved.command.clickaction;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import org.bukkit.entity.Player;

public interface Action {

    void action(Player player, BlockPosition position);
}
