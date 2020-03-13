package com.github.kuro46.scriptblockimproved.listener;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PlayerInteractListener implements Listener {
    @EventHandler
    public void onInteract(@NonNull final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ScriptBlockImproved.getInstance().getActionQueue().executeIfQueued(event.getPlayer(), event.getClickedBlock().getLocation());
        }
    }
}
