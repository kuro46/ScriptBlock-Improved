package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.ScriptExecutor;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class InteractScriptTrigger implements Listener {

    private static final TriggerName TRIGGER_RIGHT_CLICK = TriggerName.of("rclick");
    private static final TriggerName TRIGGER_LEFT_CLICK = TriggerName.of("lclick");
    private static final TriggerName TRIGGER_PRESS = TriggerName.of("press");
    private static final long INTERACT_EVENT_COOLDOWN_MS = 300;

    private final Map<Player, Long> lastExecutedTimes = new WeakHashMap<>();
    private final ScriptExecutor executor;

    private InteractScriptTrigger() {
        this.executor = ScriptExecutor.getInstance();
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        final TriggerRegistry registry = sbi.getTriggerRegistry();
        registry.register(TRIGGER_RIGHT_CLICK);
        registry.register(TRIGGER_LEFT_CLICK);
        registry.register(TRIGGER_PRESS);
        Bukkit.getPluginManager().registerEvents(this, sbi.getPlugin());
    }

    public static void listen() {
        new InteractScriptTrigger();
    }

    @EventHandler
    public void onInteract(@NonNull final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final long executionTime = System.currentTimeMillis();
        if (shouldCancelExecution(player, executionTime)) return;
        updateExecutionTime(player, executionTime);
        final BlockPosition clickedPosition = Optional.ofNullable(event.getClickedBlock())
            .map(Block::getLocation)
            .map(nonNullLoc -> BlockPosition.fromLocation(nonNullLoc))
            .orElse(null);
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                executor.execute(TRIGGER_LEFT_CLICK, player, clickedPosition);
                break;
            case RIGHT_CLICK_BLOCK:
                executor.execute(TRIGGER_RIGHT_CLICK, player, clickedPosition);
                break;
            case PHYSICAL:
                executor.execute(TRIGGER_PRESS, player, clickedPosition);
                break;
            default:
                throw new RuntimeException("Reached illegal code");
        }
    }

    private boolean shouldCancelExecution(@NonNull final Player player, final long executionTime) {
        final long lastExecutedTime = lastExecutedTimes.getOrDefault(player, -1L);
        return lastExecutedTime > (executionTime - INTERACT_EVENT_COOLDOWN_MS);
    }

    private void updateExecutionTime(@NonNull final Player player, final long executionTime) {
        lastExecutedTimes.put(player, executionTime);
    }
}
