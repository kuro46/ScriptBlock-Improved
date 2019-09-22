package com.github.kuro46.scriptblockimproved.command.clickaction;

import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import java.util.Objects;
import org.bukkit.entity.Player;

public final class ActionAdd implements Action {

    private final ParsedArgs args;

    public ActionAdd(final ParsedArgs args) {
        this.args = Objects.requireNonNull(args, "'args' cannot be null");
    }

    @Override
    public void action(final Player player, final BlockCoordinate coordinate) {
        player.performCommand(String.format("sbi addat %s %s %s %s %s %s",
            coordinate.getWorld(),
            coordinate.getX(),
            coordinate.getY(),
            coordinate.getZ(),
            args.getOrFail("trigger"),
            args.getOrFail("script")));
    }
}
