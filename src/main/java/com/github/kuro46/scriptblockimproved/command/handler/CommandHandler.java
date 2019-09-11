package com.github.kuro46.scriptblockimproved.command.handler;

import com.github.kuro46.scriptblockimproved.command.syntax.Arguments;
import java.util.List;
import org.bukkit.command.CommandSender;

public interface CommandHandler {

    Arguments getSyntax();

    void execute(CommandSender sender, ExecutionArguments args);

    List<String> complete(CommandSender sender, CompletionArguments args);
}
