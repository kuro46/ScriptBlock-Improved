package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ListHandler extends CommandHandler {

    @NonNull
    private final Scripts scripts;

    public ListHandler() {
        super(Args.empty());
        this.scripts = ScriptBlockImproved.getInstance().getScripts();
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();

        final List<BlockPosition> positions = new ArrayList<>(scripts.getPositions());
        if (positions.isEmpty()) {
            sendMessage(sender, MessageKind.ERROR, "Empty");
            return;
        }
        Collections.sort(positions);
        int count = 0;
        for (final BlockPosition position : positions) {
            sendMessage(sender,
                    "[%s] %s/%s/%s/%s",
                    ++count,
                    position.getWorld(),
                    position.getX(),
                    position.getY(),
                    position.getZ());
        }
    }
}
