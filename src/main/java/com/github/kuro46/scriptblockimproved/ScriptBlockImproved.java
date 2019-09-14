package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.command.SBICommandExecutor;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionExecutor;
import com.github.kuro46.scriptblockimproved.command.clickaction.Actions;
import com.github.kuro46.scriptblockimproved.script.ScriptExecutor;
import com.github.kuro46.scriptblockimproved.script.ScriptSaver;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class ScriptBlockImproved {

    private final OptionHandlers optionHandlers = new OptionHandlers();
    private final Placeholders placeholders = new Placeholders();
    private final Actions actions = new Actions();

    private final ScriptExecutor scriptExecutor;
    private final Path scriptsPath;
    private final Triggers triggers;
    private final Plugin plugin;
    private final Scripts scripts;
    private final ScriptSaver scriptSaver;

    ScriptBlockImproved(final Initializer plugin) {
        try {
            final Stopwatch stopwatch = Stopwatch.createStarted();

            this.plugin = plugin;
            this.scriptsPath = initScriptsPath();
            this.scripts = loadScripts();
            this.scriptSaver = new ScriptSaver(
                    plugin.getDataFolder().toPath(),
                    scripts);
            this.triggers = new Triggers(plugin);
            this.scriptExecutor = new ScriptExecutor(
                    placeholders,
                    plugin,
                    scripts,
                    optionHandlers,
                    triggers);

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

    private void registerScriptsListeners() {
        scripts.addListener(scripts -> {
            scriptSaver.saveAsync("scripts.json")
                .whenComplete((result, error) -> {
                    if (error != null) {
                        plugin.getLogger().log(
                                Level.SEVERE,
                                "Failed to save scripts."
                                + "Type /sbi save for manually save.");
                    }
                });
        });
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
        new SBICommandExecutor(scriptSaver, actions, scripts, optionHandlers, triggers);
    }

    private void registerPlaceholders() {
        placeholders.add(new PlayerPlaceholder());
        placeholders.add(new WorldPlaceholder());
    }
}
