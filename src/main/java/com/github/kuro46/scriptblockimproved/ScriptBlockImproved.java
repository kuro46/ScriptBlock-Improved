package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.command.SBICommand;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionExecutor;
import com.github.kuro46.scriptblockimproved.command.clickaction.Actions;
import com.github.kuro46.scriptblockimproved.script.ScriptExecutor;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import com.github.kuro46.scriptblockimproved.script.option.CommonOptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.Placeholders;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.PlayerPlaceholder;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.WorldPlaceholder;
import com.github.kuro46.scriptblockimproved.script.serialize.ScriptSerializer;
import com.github.kuro46.scriptblockimproved.script.serialize.UnsupportedVersionException;
import com.github.kuro46.scriptblockimproved.script.trigger.LeftClickTrigger;
import com.github.kuro46.scriptblockimproved.script.trigger.MoveTrigger;
import com.github.kuro46.scriptblockimproved.script.trigger.PressTrigger;
import com.github.kuro46.scriptblockimproved.script.trigger.RightClickTrigger;
import com.github.kuro46.scriptblockimproved.script.trigger.Triggers;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

public final class ScriptBlockImproved {

    private static ScriptBlockImproved instance;

    private final OptionHandlers optionHandlers = new OptionHandlers();
    private final Placeholders placeholders = new Placeholders();
    private final Actions actions = new Actions();

    @NonNull
    private final ScriptAutoSaver scriptAutoSaver;
    @NonNull
    @Getter
    private final Path scriptsPath;
    @Getter
    @NonNull
    private final Triggers triggers;
    @Getter
    @NonNull
    private final Plugin plugin;
    @Getter
    @NonNull
    private final Scripts scripts;
    @Getter
    @NonNull
    private final Path dataFolder;
    @Getter
    @NonNull
    private final Logger logger;

    private ScriptBlockImproved(@NonNull final Initializer plugin) throws IOException {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder().toPath();
        this.logger = plugin.getLogger();
        this.scriptsPath = initScriptsPath();
        this.scripts = loadScripts();
        this.triggers = new Triggers(plugin);
        this.scriptAutoSaver = new ScriptAutoSaver();
        initExecutor();
        registerAsService();
        registerOptionHandlers();
        registerTriggers();
        registerCommands();
        registerListeners();
        registerScriptsListeners();
        registerPlaceholders();
    }

    static void initialize(@NonNull final Initializer initializer) throws IOException {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This method must be called by primary thread");
        }
        if (instance != null) {
            throw new IllegalStateException("The plugin is already initialized");
        }
        instance = new ScriptBlockImproved(initializer);
    }

    static boolean isInitialized() {
        return instance != null;
    }

    static void dispose() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This method must be called by primary thread");
        }
        if (instance == null) {
            throw new IllegalStateException("The plugin is not initialized now");
        }
        instance.disposeInternal();
        instance = null;
    }

    private void disposeInternal() {
        scriptAutoSaver.shutdown();
    }

    private void initExecutor() {
        ScriptExecutor.init(
                placeholders,
                scripts,
                optionHandlers,
                triggers);
    }

    private Path initScriptsPath() throws IOException {
        final Path path = dataFolder.resolve("scripts.json");
        if (Files.notExists(path)) {
            final Path parent = path.getParent();
            if (parent == null) {
                throw new IOException("Cannot get the parent path of scripts.json");
            }
            Files.createDirectories(parent);
            Files.createFile(path);
        }
        return path;
    }

    private Scripts loadScripts() throws IOException {
        if (Files.size(scriptsPath) == 0) return new Scripts();
        try (final BufferedReader reader = Files.newBufferedReader(scriptsPath)) {
            return ScriptSerializer.deserialize(reader);
        } catch (final IOException | UnsupportedVersionException e) {
            throw new RuntimeException("Failed to load scripts", e);
        }
    }

    private void registerAsService() {
        Bukkit.getServicesManager().register(
            ScriptBlockImproved.class,
            this,
            plugin,
            ServicePriority.Normal);
    }

    private void registerScriptsListeners() {
        scripts.addListener(scripts -> scriptAutoSaver.saveLater());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ActionExecutor(actions), plugin);
    }

    private void registerOptionHandlers() {
        CommonOptionHandlers.registerAll(plugin, optionHandlers);
    }

    private void registerTriggers() {
        triggers.register(new MoveTrigger());
        triggers.register(new RightClickTrigger());
        triggers.register(new LeftClickTrigger());
        triggers.register(new PressTrigger());
    }

    private void registerCommands() {
        SBICommand.register(
                actions,
                scripts,
                optionHandlers,
                triggers,
                dataFolder);
    }

    private void registerPlaceholders() {
        placeholders.add(new PlayerPlaceholder());
        placeholders.add(new WorldPlaceholder());
    }

    private class ScriptAutoSaver {

        private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor(r -> {
                return new Thread(r, "script-auto-save-thread");
            });

        private ScheduledFuture<?> scheduled;

        public void saveLater() {
            if (scheduled != null) scheduled.cancel(false);
            scheduled = schedule();
        }

        private ScheduledFuture<?> schedule() {
            return executor.schedule(() -> {
                try {
                    ScriptSerializer.serialize(dataFolder.resolve("scripts.json"), scripts, true);
                } catch (IOException e) {
                    logger.log(Level.SEVERE,
                            "Failed to save scripts. Type '/sbi save' for save scripts manually.",
                            e);
                }
            }, 1, TimeUnit.SECONDS);
        }

        public void shutdown() {
            executor.shutdown();
        }
    }
}
