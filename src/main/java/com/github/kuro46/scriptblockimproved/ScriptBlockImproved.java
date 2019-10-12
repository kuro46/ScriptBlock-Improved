package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.command.SBICommand;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionExecutor;
import com.github.kuro46.scriptblockimproved.command.clickaction.Actions;
import com.github.kuro46.scriptblockimproved.common.Debouncer;
import com.github.kuro46.scriptblockimproved.script.ScriptExecutor;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import com.github.kuro46.scriptblockimproved.script.option.CommonOptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.Placeholder;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.PlaceholderGroup;
import com.github.kuro46.scriptblockimproved.script.serialize.ScriptSerializer;
import com.github.kuro46.scriptblockimproved.script.serialize.UnsupportedVersionException;
import com.github.kuro46.scriptblockimproved.script.trigger.InteractScriptTrigger;
import com.github.kuro46.scriptblockimproved.script.trigger.MoveScriptTrigger;
import com.github.kuro46.scriptblockimproved.script.trigger.SBScriptTrigger;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

public final class ScriptBlockImproved {

    private volatile static ScriptBlockImproved instance;

    @Getter
    private final OptionHandlers optionHandlers = new OptionHandlers();
    @Getter
    private final PlaceholderGroup placeholderGroup = new PlaceholderGroup();
    @Getter
    private final Actions actions = new Actions();
    @Getter
    private final TriggerRegistry triggerRegistry = new TriggerRegistry();

    @NonNull
    private final ScriptAutoSaver scriptAutoSaver;
    @NonNull
    @Getter
    private final Path scriptsPath;
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
        this.scriptAutoSaver = new ScriptAutoSaver();
    }

    private void initInternal() throws IOException {
        PermissionDetector.init(dataFolder);
        initExecutor();
        registerAsService();
        registerOptionHandlers();
        initTriggers();
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
        instance.initInternal();
    }

    static boolean isInitialized() {
        return instance != null;
    }

    public static ScriptBlockImproved getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Not initialized yet");
        }
        return instance;
    }

    static void dispose() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This method must be called by primary thread");
        }
        if (instance == null) {
            throw new IllegalStateException("The plugin is not initialized now");
        }
        instance = null;
    }

    private void initExecutor() {
        ScriptExecutor.init();
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

    private void initTriggers() {
        InteractScriptTrigger.listen();
        MoveScriptTrigger.listen();
        SBScriptTrigger.listen();
    }

    private void registerCommands() {
        SBICommand.register();
    }

    private void registerPlaceholders() {
        placeholderGroup.add(Placeholder.builder()
                .name("player")
                .factory(data -> data.getPlayer().getName())
                .build());
        placeholderGroup.add(Placeholder.builder()
                .name("world")
                .factory(data -> data.getPosition().getWorld())
                .build());
    }

    private class ScriptAutoSaver {

        private final Debouncer debouncer = new Debouncer(this::save, 1, TimeUnit.SECONDS);

        public void saveLater() {
            debouncer.runLater();
        }

        private void save() {
            try {
                ScriptSerializer.serialize(dataFolder.resolve("scripts.json"), scripts, true);
            } catch (IOException e) {
                logger.log(Level.SEVERE,
                        "Failed to save scripts. Type '/sbi save' for save scripts manually.",
                        e);
            }
        }
    }
}
