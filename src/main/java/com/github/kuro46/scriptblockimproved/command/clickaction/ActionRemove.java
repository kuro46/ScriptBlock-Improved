package com.github.kuro46.scriptblockimproved.command.clickaction;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import org.bukkit.entity.Player;

public final class ActionRemove implements Action {

    @Override
    public void action(final Player player, final BlockPosition position) {
        player.performCommand(String.format("sbi deleteat %s %s %s %s",
                    position.getWorld(),
                    position.getX(),
                    position.getY(),
                    position.getZ()));
    }
}
