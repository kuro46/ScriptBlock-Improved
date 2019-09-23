package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ListHandler extends CommandHandler {

    private final Scripts scripts;

    public ListHandler(final Scripts scripts) {
        super(Args.empty());

        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
    }

    @Override
    public void execute(
            final CommandManager manager,
            final CommandSender sender,
            final ParsedArgs empty) {
        final List<BlockCoordinate> coordinates = new ArrayList<>(scripts.getCoordinates());
        Collections.sort(coordinates);
        int count = 0;
        for (final BlockCoordinate coordinate : coordinates) {
            sendMessage(sender,
                    "[%s] %s/%s/%s/%s",
                    ++count,
                    coordinate.getWorld(),
                    coordinate.getX(),
                    coordinate.getY(),
                    coordinate.getZ());
        }
    }
}
