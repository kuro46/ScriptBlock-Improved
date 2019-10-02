package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.command.clickaction.ActionView;
import com.github.kuro46.scriptblockimproved.command.clickaction.Actions;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ViewHandler extends CommandHandler {

    private final Actions actions;

    public ViewHandler(final Actions actions) {
        super(Args.empty());

        this.actions = Objects.requireNonNull(actions, "'actions' cannot be null");
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();

        if (!(sender instanceof Player)) {
            sendMessage(sender, MessageKind.ERROR, "Cannot perform this command from the console");
            return;
        }
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to view information about scripts in the block");
        actions.add(player, new ActionView());
    }
}
