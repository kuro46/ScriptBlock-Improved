package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.command.SBICommandExecutor;
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
import com.google.common.base.Stopwatch;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

public final class ScriptBlockImproved {

    private static ScriptBlockImproved instance;

    private final OptionHandlers optionHandlers = new OptionHandlers();
    private final Placeholders placeholders = new Placeholders();
    private final Actions actions = new Actions();

    private final ScriptExecutor scriptExecutor;
    private final ScriptAutoSaver scriptAutoSaver;
    private final Path scriptsPath;
    private final Triggers triggers;
    private final Plugin plugin;
    private final Scripts scripts;

    private ScriptBlockImproved(final Initializer plugin) {
        try {
            final Stopwatch stopwatch = Stopwatch.createStarted();

            this.plugin = plugin;
            this.scriptsPath = initScriptsPath();
            this.scripts = loadScripts();
            this.triggers = new Triggers(plugin);
            this.scriptExecutor = new ScriptExecutor(
                    placeholders,
                    plugin,
                    scripts,
                    optionHandlers,
                    triggers);
            this.scriptAutoSaver = new ScriptAutoSaver(
                    plugin.getLogger(),
                    scripts,
                    plugin.getDataFolder().toPath().resolve("scripts.json"));

            registerAsService();
            registerOptionHandlers();
            registerTriggers();
            registerCommandExecutor();
            registerListeners();
            registerScriptsListeners();
            registerPlaceholders();

            stopwatch.stop();
            plugin.getLogger().info(String.format("Enabled (took %s)", stopwatch));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize the plugin", e);
        }
    }

    @SuppressFBWarnings("LI_LAZY_INIT_STATIC") // This method always called by primary thread
    static void initialize(final Initializer Initializer) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This method must be called by primary thread");
        }
        if (instance != null) {
            throw new IllegalStateException("The plugin is already initialized");
        }
        instance = new ScriptBlockImproved(Initializer);
    }

    public OptionHandlers getOptionHandlers() {
        return optionHandlers;
    }

    public Triggers getTriggers() {
        return triggers;
    }

    public Scripts getScripts() {
        return scripts;
    }

    public Placeholders getPlaceholders() {
        return placeholders;
    }

    private Path initScriptsPath() throws IOException {
        final Path path = plugin.getDataFolder().toPath().resolve("scripts.json");
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
        if (Files.size(scriptsPath) == 0) {
            return new Scripts();
        }
        try (BufferedReader reader = Files.newBufferedReader(scriptsPath)) {
            return ScriptSerializer.deserialize(reader);
        } catch (IOException | UnsupportedVersionException e) {
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

    private void registerCommandExecutor() {
        new SBICommandExecutor(
                actions,
                scripts,
                optionHandlers,
                triggers,
                plugin.getDataFolder().toPath());
    }

    private void registerPlaceholders() {
        placeholders.add(new PlayerPlaceholder());
        placeholders.add(new WorldPlaceholder());
    }

    private static class ScriptAutoSaver {

        private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor(r -> {
                return new Thread(r, "script-auto-save-thread");
            });
        private final Logger logger;
        private final Scripts scripts;
        private final Path path;

        private ScheduledFuture<?> scheduled;

        public ScriptAutoSaver(final Logger logger, final Scripts scripts, final Path path) {
            this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
            this.logger = Objects.requireNonNull(logger, "'logger' cannot be null");
            this.path = Objects.requireNonNull(path, "'path' cannot be null");
        }

        public void saveLater() {
            if (scheduled != null) {
                scheduled.cancel(false);
            }
            scheduled = schedule();
        }

        private ScheduledFuture<?> schedule() {
            return executor.schedule(() -> {
                try {
                    ScriptSerializer.serialize(path, scripts, true);
                } catch (IOException e) {
                    logger.log(Level.SEVERE,
                            "Failed to save scripts. Type '/sbi save' for save scripts manually.",
                            e);
                }
            }, 1, TimeUnit.SECONDS);
        }
    }
}
