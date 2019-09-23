package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.command.clickaction.ActionCreate;
import com.github.kuro46.scriptblockimproved.command.clickaction.Actions;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CreateHandler extends CommandHandler {

    private final Actions actions;

    public CreateHandler(final Actions actions) {
        super(Args.builder()
                .required("trigger")
                .required("script")
                .build());

        this.actions = Objects.requireNonNull(actions, "'actions' cannot be null");
    }

    @Override
    public void execute(
            final CommandManager manager,
            final CommandSender sender,
            final ParsedArgs args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender,
                    MessageKind.ERROR,
                    "Cannot perform this command from the console");
            return;
        }
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to create script to the block");
        actions.add(player, new ActionCreate(args));
    }
}
