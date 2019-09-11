package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public final class MoveTrigger implements Trigger {

    private final Map<Player, BlockCoordinate> lastCoordinates = new WeakHashMap<>();

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
        final BlockCoordinate coordinate = BlockCoordinate.fromLocation(player.getLocation());
        if (shouldCancelExecution(player, coordinate)) return EventValidateResult.invalid();
        updateCoordinate(player, coordinate);
        return EventValidateResult.valid(new EventData(coordinate, player));
    }

    private boolean shouldCancelExecution(final Player player, final BlockCoordinate coordinate) {
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");

        final BlockCoordinate lastCoordinate = lastCoordinates.get(player);
        if (lastCoordinate == null) return false;
        return lastCoordinate.equals(coordinate);
    }

    private void updateCoordinate(final Player player, final BlockCoordinate coordinate) {
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");

        lastCoordinates.put(player, coordinate);
    }
}
