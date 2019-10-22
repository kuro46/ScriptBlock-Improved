package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public final class SBScriptTrigger {

    private final Map<Player, BlockPosition> lastWalkedPositions = new WeakHashMap<>();
    private final RegisteredTrigger interactTrigger;
    private final RegisteredTrigger walkTrigger;

    private SBScriptTrigger() {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        final Plugin plugin = sbi.getPlugin();
        final TriggerRegistry registry = sbi.getTriggerRegistry();
        this.interactTrigger = registry.register("sbinteract");
        this.walkTrigger = registry.register("sbwalk");
        initInteract(plugin, registry);
        initMove(plugin, registry);
    }

    public static void listen() {
        new SBScriptTrigger();
    }

    private void initInteract(
            @NonNull final Plugin plugin,
            @NonNull final TriggerRegistry registry) {
        final InteractListener listener = new InteractListener();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        interactTrigger.onUnregistered(() -> HandlerList.unregisterAll(listener));
    }

    private void initMove(@NonNull final Plugin plugin, @NonNull final TriggerRegistry registry) {
        final MoveListener listener = new MoveListener();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        walkTrigger.onUnregistered(() -> HandlerList.unregisterAll(listener));
    }

    public void onInteract(@NonNull final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final BlockPosition clickedPosition = Optional.ofNullable(event.getClickedBlock())
            .map(BlockPosition::fromBlock)
            .orElse(null);
        if (clickedPosition == null) return;
        interactTrigger.executeIfAvailable(player, clickedPosition);
    }

    public void onMove(@NonNull final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Block walkingBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (walkingBlock.getType() == Material.AIR) return;

        final BlockPosition walkingPos = BlockPosition.fromBlock(walkingBlock);
        final BlockPosition lastWalkedPos = lastWalkedPositions.get(player);
        if (lastWalkedPos != null && lastWalkedPos.equals(walkingPos)) return;
        lastWalkedPositions.put(player, walkingPos);

        walkTrigger.executeIfAvailable(player, walkingPos);
    }

    private final class InteractListener implements Listener {

        @EventHandler
        public void onInteract(@NonNull final PlayerInteractEvent event) {
            SBScriptTrigger.this.onInteract(event);
        }
    }

    private final class MoveListener implements Listener {

        @EventHandler
        public void onMove(@NonNull final PlayerMoveEvent event) {
            SBScriptTrigger.this.onMove(event);
        }
    }
}
