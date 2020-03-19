package xyz.shirokuro.scriptblockimproved.listener;

import xyz.shirokuro.scriptblockimproved.ActionQueue;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.ScriptHandler;
import xyz.shirokuro.scriptblockimproved.Trigger;
import xyz.shirokuro.scriptblockimproved.TriggerData;
import xyz.shirokuro.scriptblockimproved.TriggerRegistry;
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

    private final Trigger sbInteractTrigger = new Trigger("sbinteract", () -> {
    });
    private final Trigger clickRightTrigger = new Trigger("rclick", () -> {
    });
    private final Trigger clickLeftTrigger = new Trigger("lclick", () -> {
    });
    private final Trigger pressTrigger = new Trigger("press", () -> {
    });

    public PlayerInteractListener(@NonNull final TriggerRegistry triggerRegistry) {
        triggerRegistry.register(sbInteractTrigger);
        triggerRegistry.register(clickRightTrigger);
        triggerRegistry.register(clickLeftTrigger);
        triggerRegistry.register(pressTrigger);
    }

    @EventHandler
    public void onInteract(@NonNull final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ActionQueue actionQueue = ScriptBlockImproved.getInstance().getActionQueue();
        final ScriptHandler scriptHandler = ScriptBlockImproved.getInstance().getScriptHandler();
        if (event.getClickedBlock() != null && !sbInteractTrigger.isUnregistered()) {
            final TriggerData triggerData = TriggerData.builder()
                .trigger(sbInteractTrigger)
                .shouldSuppress(false)
                .event(event)
                .build();
            scriptHandler.handle(player, BlockPosition.ofLocation(event.getClickedBlock().getLocation()), triggerData);
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (actionQueue.isQueued(player)) {
                actionQueue.executeIfQueued(player, event.getClickedBlock().getLocation());
                return;
            }
            handle(event, clickRightTrigger);
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            handle(event, clickLeftTrigger);
        } else if (event.getAction() == Action.PHYSICAL) {
            handle(event, pressTrigger);
        }
    }

    private void handle(@NonNull final PlayerInteractEvent event, @NonNull final Trigger trigger) {
        if (trigger.isUnregistered()) {
            return;
        }
        final BlockPosition position = BlockPosition.ofLocation(event.getClickedBlock().getLocation());
        final boolean shouldSuppress = isInInterval(event.getPlayer());
        if (!shouldSuppress) {
            addInterval(event.getPlayer());
        }
        final ScriptHandler scriptHandler = ScriptBlockImproved.getInstance().getScriptHandler();
        scriptHandler.handle(event.getPlayer(), position, TriggerData.builder().shouldSuppress(shouldSuppress).event(event).trigger(trigger).build());
    }

    private boolean isInInterval(@NonNull final Player player) {
        return interval.asMap().containsKey(player);
    }

    private void addInterval(@NonNull final Player player) {
        interval.put(player, DUMMY);
    }
}
