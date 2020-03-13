package com.github.kuro46.scriptblockimproved.listener;

import com.github.kuro46.scriptblockimproved.ActionQueue;
import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.ScriptHandler;
import com.github.kuro46.scriptblockimproved.TriggerInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PlayerInteractListener implements Listener {

    private static final Object DUMMY = new Object();
    private final Cache<Player, Object> interval = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMillis(300))
        .build();

    @EventHandler
    public void onInteract(@NonNull final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ActionQueue actionQueue = ScriptBlockImproved.getInstance().getActionQueue();
        final ScriptHandler scriptHandler = ScriptBlockImproved.getInstance().getScriptHandler();
        if (event.getClickedBlock() != null) {
            final TriggerInfo triggerInfo = TriggerInfo.builder()
                .name("sbinteract")
                .shouldSuppress(false)
                .event(event)
                .build();
            scriptHandler.handle(player, BlockPosition.ofLocation(event.getClickedBlock().getLocation()), triggerInfo);
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (actionQueue.isQueued(player)) {
                actionQueue.executeIfQueued(player, event.getClickedBlock().getLocation());
                return;
            }
            handle(event, "rclick");
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            handle(event, "lclick");
        } else if (event.getAction() == Action.PHYSICAL) {
            handle(event, "press");
        }
    }

    private void handle(@NonNull final PlayerInteractEvent event, @NonNull final String triggerName) {
        final BlockPosition position = BlockPosition.ofLocation(event.getClickedBlock().getLocation());
        final boolean shouldSuppress = isInInterval(event.getPlayer());
        if (!shouldSuppress) {
            addInterval(event.getPlayer());
        }
        final ScriptHandler scriptHandler = ScriptBlockImproved.getInstance().getScriptHandler();
        scriptHandler.handle(event.getPlayer(), position, TriggerInfo.builder().shouldSuppress(shouldSuppress).event(event).name(triggerName).build());
    }

    private boolean isInInterval(@NonNull final Player player) {
        return interval.asMap().containsKey(player);
    }

    private void addInterval(@NonNull final Player player) {
        interval.put(player, DUMMY);
    }
}
