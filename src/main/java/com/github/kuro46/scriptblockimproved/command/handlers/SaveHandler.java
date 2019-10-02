package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import com.github.kuro46.scriptblockimproved.script.serialize.ScriptSerializer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class SaveHandler extends CommandHandler {

    private final Path dataFolder;
    private final Scripts scripts;

    public SaveHandler(final Path dataFolder, final Scripts scripts) {
        super(Args.builder()
                .optional("fileName")
                .build());

        this.dataFolder = Objects.requireNonNull(dataFolder, "'dataFolder' cannot be null");
        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
    }

    @Override
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();

        final String fileName = args.get("fileName").orElse("scripts.json");
        final boolean canOverwrite = fileName.equals("scripts.json");

        sendMessage(sender,
                "Saving scripts into '/ScriptBlock-Improved/%s'",
                fileName);
        new Thread(() -> {
            try {
                ScriptSerializer.serialize(dataFolder.resolve(fileName), scripts, canOverwrite);
                sendMessage(sender, MessageKind.SUCCESS, "Successfully saved");
            } catch (final IOException e) {
                sendMessage(sender, MessageKind.ERROR, "Save failed!");
            }
        }, "sbi-command-sbi_save").start();
    }
}
