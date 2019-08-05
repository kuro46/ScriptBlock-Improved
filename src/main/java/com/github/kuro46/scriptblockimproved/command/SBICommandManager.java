package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.StringConverters;
import com.github.kuro46.commandutility.handle.CastError;
import com.github.kuro46.commandutility.handle.Command;
import com.github.kuro46.commandutility.handle.CommandManager;
import com.github.kuro46.commandutility.handle.CommandSections;
import com.github.kuro46.commandutility.handle.FallbackCommandHandler;
import com.github.kuro46.commandutility.handle.HelpCommandHandler;
import com.github.kuro46.commandutility.syntax.ParseErrorReason;
import org.bukkit.command.CommandSender;

public class SBICommandManager extends CommandManager {

    private SBICommandManager(FallbackCommandHandler fallback, StringConverters converters) {
        super(fallback, converters);
    }

    public static void init() {
        final SBICommandManager commandManager =
                new SBICommandManager(new SBIFallbackHandler(), initConverters());
        commandManager.registerCommand(
                new Command(
                        CommandSections.fromString("sbi help"),
                        new HelpCommandHandler(CommandSections.fromString("sbi"))));

        // TODO add handlers
    }

    private static StringConverters initConverters() {
        final StringConverters converters = new StringConverters();
        converters.registerDefaults();
        return converters;
    }

    @Override
    public void handleCastError(CommandSender sender, CastError error) {
        sender.sendMessage(error.name());
    }

    @Override
    public void handleParseError(CommandSender sender, ParseErrorReason error) {
        sender.sendMessage(error.name());
    }
}
