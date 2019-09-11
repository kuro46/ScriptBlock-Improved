package com.github.kuro46.scriptblockimproved.command.handler;

import org.bukkit.command.CommandSender;

public interface CommandsListener {

    void onInvalidSyntax(CommandSender sender);

    void onUnknownCommand(CommandSender sender);
}
