package com.github.kuro46.scriptblockimproved.command.clickaction;

import com.github.kuro46.scriptblockimproved.command.handler.ExecutionArguments;
import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

public final class ActionCreate implements Action {

    private final ExecutionArguments args;

    public ActionCreate(final ExecutionArguments args) {
        this.args = Objects.requireNonNull(args, "'args' cannot be null");
    }

    @Override
    public void action(final Player player, final BlockCoordinate coordinate) {
        player.performCommand(String.format("sbi createat %s %s %s %s %s",
                    coordinate.getWorld(),
                    coordinate.getX(),
                    coordinate.getY(),
                    coordinate.getZ(),
                    args.stream().collect(Collectors.joining(" "))));
    }
}
