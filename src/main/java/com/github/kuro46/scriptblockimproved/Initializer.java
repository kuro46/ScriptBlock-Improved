package com.github.kuro46.scriptblockimproved;

import org.bukkit.plugin.java.JavaPlugin;

public final class Initializer extends JavaPlugin {

    @Override
    public void onEnable() {
        new ScriptBlockImproved(this);
    }
}
