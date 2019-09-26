package com.github.kuro46.scriptblockimproved;

import com.google.common.base.Stopwatch;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Initializer extends JavaPlugin {

    @Override
    public void onEnable() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            ScriptBlockImproved.initialize(this);
        } catch (final Exception e) {
            getLogger().log(Level.SEVERE, "Initialization failed. Disabling...", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info(String.format("Enabled (took %s)", stopwatch));
    }

    @Override
    public void onDisable() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        if (ScriptBlockImproved.isInitialized()) {
            ScriptBlockImproved.dispose();
        }
        getLogger().info(String.format("Disabled (took %s)", stopwatch));
    }
}
