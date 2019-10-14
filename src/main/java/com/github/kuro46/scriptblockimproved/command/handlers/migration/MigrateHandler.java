package com.github.kuro46.scriptblockimproved.command.handlers.migration;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CommandHandler;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class MigrateHandler extends CommandHandler {

    private static final String MIGRATED_MARKER_FILE_NAME = "mark_migrated_from_sb";

    private final Scripts scripts;
    private final Path dataFolder;

    public MigrateHandler() {
        super(Args.empty());
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        this.scripts = sbi.getScripts();
        this.dataFolder = sbi.getDataFolder();
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        sendMessage(sender, "Migrating...");
        new Thread(() -> {
            if (hasMigrated()) {
                sendMessage(sender, MessageKind.ERROR, "Cannot migrate from ScriptBlock!");
                sendMessage(sender, MessageKind.ERROR, "SBI has migrated in the past.");
                sendMessage(sender,
                        MessageKind.ERROR,
                        "If you want to re-migrate, "
                        + "please delete file '%s' in 'plugins/ScriptBlock-Improved/'.",
                        MIGRATED_MARKER_FILE_NAME);
                return;
            }
            final Scripts loadedScripts = new Scripts();
            try {
                for (final EventType eventType : EventType.values()) {
                    sendMessage(sender, "Migrating %s scripts...", eventType.name().toLowerCase());
                    loadScripts(eventType, loadedScripts);
                }
            } catch (final MigrationException e) {
                sendMessage(sender, MessageKind.ERROR, "Failed to migrate scripts: "
                        + e.getMessage());
                return;
            }
            scripts.addAll(loadedScripts);
            sendMessage(sender, MessageKind.SUCCESS, "Successfully migrated!");
            try {
                markAsMigrated();
            } catch (final IOException e) {
                ScriptBlockImproved.getInstance().getLogger()
                    .log(Level.SEVERE, "Failed to mark as migrated from ScriptBlock", e);
            }
        }, "sbi-migrator-thread").start();
    }

    private void loadScripts(
            @NonNull final EventType eventType,
            @NonNull final Scripts dest) throws MigrationException {
        final Path filePath = dataFolder
            .resolve("../ScriptBlock/BlocksData/")
            .resolve(eventType.getFileName());
        final Scripts loadedScripts = SBScriptLoader.load(eventType.getTriggerName(), filePath);
        // merge loaded scripts to current scripts
        dest.addAll(loadedScripts);
    }

    private void markAsMigrated() throws IOException {
        Files.createFile(dataFolder.resolve(MIGRATED_MARKER_FILE_NAME));
    }

    private boolean hasMigrated() {
        return Files.exists(dataFolder.resolve(MIGRATED_MARKER_FILE_NAME));
    }
}
