package com.github.kuro46.scriptblockimproved;

import org.bukkit.plugin.java.JavaPlugin;

public final class Initializer extends JavaPlugin {

    @Override
    public void onEnable() {
        ScriptBlockImproved.initialize(this);
    }

    @Override
    public void onDisable() {
        ScriptBlockImproved.dispose();
    }
}
