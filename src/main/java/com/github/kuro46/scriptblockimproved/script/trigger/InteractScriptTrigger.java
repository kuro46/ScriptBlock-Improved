package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public final class InteractScriptTrigger implements Listener {

    private static final long INTERACT_EVENT_COOLDOWN_MS = 300;

    private final Map<Player, Long> lastExecutedTimes = new WeakHashMap<>();
    private final RegisteredTrigger rightClickTrigger;
    private final RegisteredTrigger leftClickTrigger;
    private final RegisteredTrigger pressTrigger;

    private InteractScriptTrigger() {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        final TriggerRegistry registry = sbi.getTriggerRegistry();
        this.rightClickTrigger = registry.register("rclick");
        this.leftClickTrigger = registry.register("lclick");
        this.pressTrigger = registry.register("press");

        final RegisteredTriggerGroup group = RegisteredTriggerGroup.builder()
            .add(rightClickTrigger)
            .add(leftClickTrigger)
            .add(pressTrigger)
            .build();
        group.onUnregisteredAll(() -> HandlerList.unregisterAll(this));
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
            .map(BlockPosition::fromBlock)
            .orElse(null);
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                leftClickTrigger.executeIfAvailable(player, clickedPosition);
                break;
            case RIGHT_CLICK_BLOCK:
                rightClickTrigger.executeIfAvailable(player, clickedPosition);
                break;
            case PHYSICAL:
                pressTrigger.executeIfAvailable(player, clickedPosition);
                break;
            default:
                return;
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
