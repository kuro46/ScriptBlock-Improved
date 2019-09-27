package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class RightClickTrigger implements Trigger {

    private static final long EXECUTION_COOLTIME_MS = 300;

    private final Map<Player, Long> lastExecutedTimes = new WeakHashMap<>();

    @Override
    public TriggerName getName() {
        return TriggerName.of("rclick");
    }

    @Override
    public Class<? extends Event> getTarget() {
        return PlayerInteractEvent.class;
    }

    @Override
    public EventValidateResult validate(final Event event) {
        final PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
        final Player player = interactEvent.getPlayer();
        final long executionTime = System.currentTimeMillis();

        if (shouldCancelExecution(player, executionTime)) return EventValidateResult.invalid();
        updateExecutionTime(player, executionTime);

        final Action action = interactEvent.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            return EventValidateResult.valid(new EventData(
                BlockPosition.fromLocation(interactEvent.getClickedBlock().getLocation()),
                player));
        } else {
            return EventValidateResult.invalid();
        }
    }

    private boolean shouldCancelExecution(final Player player, final long executionTime) {
        Objects.requireNonNull(player, "'player' cannot be null");

        final long lastExecutedTime = lastExecutedTimes.getOrDefault(player, -1L);
        return lastExecutedTime > (executionTime - EXECUTION_COOLTIME_MS);
    }

    private void updateExecutionTime(final Player player, final long executionTime) {
        Objects.requireNonNull(player, "'player' cannot be null");

        lastExecutedTimes.put(player, executionTime);
    }
}
