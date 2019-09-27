package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public final class MoveTrigger implements Trigger {

    private final Map<Player, BlockPosition> lastPositions = new WeakHashMap<>();

    @Override
    public TriggerName getName() {
        return TriggerName.of("move");
    }

    @Override
    public Class<? extends Event> getTarget() {
        return PlayerMoveEvent.class;
    }

    @Override
    public EventValidateResult validate(final Event event) {
        final PlayerMoveEvent moveEvent = (PlayerMoveEvent) event;
        final Player player = moveEvent.getPlayer();
        final BlockPosition position = BlockPosition.fromLocation(player.getLocation());
        if (shouldCancelExecution(player, position)) return EventValidateResult.invalid();
        updatePosition(player, position);
        return EventValidateResult.valid(new EventData(position, player));
    }

    private boolean shouldCancelExecution(final Player player, final BlockPosition position) {
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(position, "'position' cannot be null");

        final BlockPosition lastPosition = lastPositions.get(player);
        if (lastPosition == null) return false;
        return lastPosition.equals(position);
    }

    private void updatePosition(final Player player, final BlockPosition position) {
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(position, "'position' cannot be null");

        lastPositions.put(player, position);
    }
}
