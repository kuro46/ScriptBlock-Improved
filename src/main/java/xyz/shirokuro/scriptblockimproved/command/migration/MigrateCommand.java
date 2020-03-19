package xyz.shirokuro.scriptblockimproved.command.migration;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.ExecutionData;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.ScriptList;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.storage.NoOpStorage;
import com.google.common.util.concurrent.Futures;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;
import java.util.logging.Level;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class MigrateCommand extends Command {

    private static final String MIGRATED_MARKER_FILE_NAME = "mark_migrated_from_sb";

    public MigrateCommand() {
        super("migrate", Args.empty());
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        MessageUtils.sendMessage(sender, "Migrating...");
        new Thread(() -> {
            if (hasMigrated()) {
                MessageUtils.sendMessage(sender, MessageKind.ERROR, "Cannot migrate from ScriptBlock!");
                MessageUtils.sendMessage(sender, MessageKind.ERROR, "SBI has migrated in the past.");
                MessageUtils.sendMessage(sender,
                    MessageKind.ERROR,
                    "If you want to re-migrate, "
                        + "please delete file '%s' in 'plugins/ScriptBlock-Improved/'.",
                    MIGRATED_MARKER_FILE_NAME);
                return;
            }
            final ScriptList loadedScriptMap;
            try {
                loadedScriptMap = ScriptList.load(new NoOpStorage());
            } catch (IOException e) {
                throw new RuntimeException("Unreachable", e);
            }
            try {
                for (final EventType eventType : EventType.values()) {
                    sendMessage(sender, "Migrating %s scripts...", eventType.name().toLowerCase());
                    loadScripts(eventType, loadedScriptMap);
                }
            } catch (final MigrationException e) {
                MessageUtils.sendMessage(sender, MessageKind.ERROR, "Failed to migrate scripts: "
                    + e.getMessage());
                return;
            }
            final Future<Object> future = Bukkit.getScheduler().callSyncMethod(ScriptBlockImproved.getInstance().getPlugin(), () -> {
                ScriptBlockImproved.getInstance().getScriptList().addAll(loadedScriptMap);
                return null;
            });
            Futures.getUnchecked(future);
            MessageUtils.sendMessage(sender, MessageKind.SUCCESS, "Successfully migrated!");
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
        @NonNull final ScriptList dest) throws MigrationException {
        final Path filePath = ScriptBlockImproved.getInstance().getPlugin().getDataFolder().toPath()
            .resolve("../ScriptBlock/BlocksData/")
            .resolve(eventType.getFileName());
        final ScriptList loadedScriptMap = SBScriptLoader.load(eventType.getTriggerName(), filePath);
        // merge loaded scripts to current scripts
        dest.addAll(loadedScriptMap);
    }

    private void markAsMigrated() throws IOException {
        Files.createFile(ScriptBlockImproved.getInstance().getPlugin().getDataFolder().toPath().resolve(MIGRATED_MARKER_FILE_NAME));
    }

    private boolean hasMigrated() {
        return Files.exists(ScriptBlockImproved.getInstance().getPlugin().getDataFolder().toPath().resolve(MIGRATED_MARKER_FILE_NAME));
    }
}
