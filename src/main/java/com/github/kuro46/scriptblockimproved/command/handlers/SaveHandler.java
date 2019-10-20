package com.github.kuro46.scriptblockimproved.command.handlers;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.ScriptMap;
import com.github.kuro46.scriptblockimproved.script.serialize.ScriptSerializer;
import java.io.IOException;
import java.nio.file.Path;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class SaveHandler extends CommandHandler {

    @NonNull
    private final Path dataFolder;
    @NonNull
    private final ScriptMap scripts;

    public SaveHandler() {
        super(Args.builder()
                .optional("fileName")
                .build());
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        this.dataFolder = sbi.getDataFolder();
        this.scripts = sbi.getScripts();
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
