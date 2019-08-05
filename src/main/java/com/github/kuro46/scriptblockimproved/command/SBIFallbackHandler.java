package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.handle.CommandManager;
import com.github.kuro46.commandutility.handle.CommandSections;
import com.github.kuro46.commandutility.handle.CommandSenderType;
import com.github.kuro46.commandutility.handle.FallbackCommandHandler;
import java.util.List;
import org.bukkit.command.CommandSender;

public class SBIFallbackHandler extends FallbackCommandHandler {

    @Override
    public CommandSenderType getSenderType() {
        return CommandSenderType.ANY;
    }

    @Override
    public void handleFallback(
            CommandManager caller,
            CommandSender sender,
            CommandSections sections,
            List<String> args) {
        sender.sendMessage("Unknown command. Type '/sbi help' for help.");
    }
}
