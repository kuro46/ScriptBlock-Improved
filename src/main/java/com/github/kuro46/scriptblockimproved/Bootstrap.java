package com.github.kuro46.scriptblockimproved;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bootstrap extends JavaPlugin {
    @Override
    public void onEnable() {
        boolean success = false;
        try {
            ScriptBlockImproved.init(this);
            success = true;
        } catch (ScriptBlockImproved.InitException e) {
            getLogger().log(Level.SEVERE, "Failed to initialize the plugin.", e);
        }
        if (!success) {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
