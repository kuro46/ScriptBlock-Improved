package com.github.kuro46.scriptblockimproved.command.clickaction;

import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class ActionAdd implements Action {

    @NonNull
    private final ParsedArgs args;

    @Override
    public void action(final Player player, final BlockPosition position) {
        player.performCommand(String.format("sbi addat %s %s %s %s %s %s",
            position.getWorld(),
            position.getX(),
            position.getY(),
            position.getZ(),
            args.getOrFail("trigger"),
            args.getOrFail("script")));
    }
}
