package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.ScriptExecutor;
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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public final class SBScriptTrigger implements Listener {

    private static final TriggerName TRIGGER_INTERACT = TriggerName.of("sbinteract");
    private static final TriggerName TRIGGER_WALK = TriggerName.of("sbwalk");

    private final Map<Player, BlockPosition> lastWalkedPositions = new WeakHashMap<>();
    private final ScriptExecutor executor;

    private SBScriptTrigger() {
        this.executor = ScriptExecutor.getInstance();
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        final TriggerRegistry registry = sbi.getTriggerRegistry();
        registry.register(TRIGGER_INTERACT);
        registry.register(TRIGGER_WALK);
        Bukkit.getPluginManager().registerEvents(this, sbi.getPlugin());
    }

    public static void listen() {
        new SBScriptTrigger();
    }

    @EventHandler
    public void onInteract(@NonNull final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final BlockPosition clickedPosition = Optional.ofNullable(event.getClickedBlock())
            .map(BlockPosition::fromBlock)
            .orElse(null);
        if (clickedPosition == null) return;
        executor.execute(TRIGGER_INTERACT, player, clickedPosition);
    }

    @EventHandler
    public void onMove(@NonNull final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Block walkingBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (walkingBlock.getType() == Material.AIR) return;

        final BlockPosition walkingPos = BlockPosition.fromBlock(walkingBlock);
        final BlockPosition lastWalkedPos = lastWalkedPositions.get(player);
        if (lastWalkedPos != null && lastWalkedPos.equals(walkingPos)) return;
        lastWalkedPositions.put(player, walkingPos);

        executor.execute(TRIGGER_WALK, player, walkingPos);
    }
}
