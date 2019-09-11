package com.github.kuro46.scriptblockimproved.command.clickaction;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import org.bukkit.entity.Player;

public final class ActionDelete implements Action {

    @Override
    public void action(final Player player, final BlockCoordinate coordinate) {
        player.performCommand(String.format("sbi deleteat %s %s %s %s",
                    coordinate.getWorld(),
                    coordinate.getX(),
                    coordinate.getY(),
                    coordinate.getZ()));
    }
}
