package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.command.SBICommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SBIPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SBICommandManager.init();
    }
}
