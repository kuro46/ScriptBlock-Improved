package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.PermissionDetector;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class PermMapHandler extends CommandHandler {

    public PermMapHandler() {
        super(Args.builder()
                .required("permission")
                .required("command")
                .build());
    }

    @Override
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();
        final String permission = args.getOrFail("permission");
        final String command = args.getOrFail("command");
        PermissionDetector.getInstance().associate(command, permission);
        sendMessage(sender, "Mapped");
    }
}
