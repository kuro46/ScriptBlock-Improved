package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public final class MoveScriptTrigger implements Listener {

    private final Map<Player, BlockPosition> lastPositions = new WeakHashMap<>();
    private final RegisteredTrigger trigger;

    private MoveScriptTrigger() {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        final TriggerRegistry registry = sbi.getTriggerRegistry();
        this.trigger = registry.register("move");
        this.trigger.onUnregistered(() -> HandlerList.unregisterAll(this));
        Bukkit.getPluginManager().registerEvents(this, sbi.getPlugin());
    }

    public static void listen() {
        new MoveScriptTrigger();
    }

    @EventHandler
    public void onMove(@NonNull final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final BlockPosition position = BlockPosition.fromLocation(player.getLocation());
        if (shouldCancelExecution(player, position)) return;
        updatePosition(player, position);
        trigger.executeIfAvailable(player, position);
    }

    private boolean shouldCancelExecution(
            @NonNull final Player player,
            @NonNull final BlockPosition position) {
        final BlockPosition lastPosition = lastPositions.get(player);
        if (lastPosition == null) return false;
        return lastPosition.equals(position);
    }

    private void updatePosition(
            @NonNull final Player player,
            @NonNull final BlockPosition position) {
        lastPositions.put(player, position);
    }
}
