package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public final class MoveTrigger extends Trigger<PlayerMoveEvent> {

    private final Map<Player, BlockPosition> lastPositions = new WeakHashMap<>();

    private MoveTrigger() {
        super(
            ScriptBlockImproved.getInstance().getPlugin(),
            PlayerMoveEvent.class,
            "move"
        );
    }

    public static void register() {
        new MoveTrigger();
    }

    @Override
    public boolean validateCondition(@NonNull final PlayerMoveEvent event) {
        return true;
    }

    @Override
    public BlockPosition retrievePosition(@NonNull final PlayerMoveEvent event) {
        return BlockPosition.fromLocation(event.getPlayer().getLocation());
    }

    @Override
    public Player retrievePlayer(@NonNull final PlayerMoveEvent event) {
        return event.getPlayer();
    }

    @Override
    public boolean shouldSuppress(
        @NonNull final PlayerMoveEvent event,
        @NonNull final BlockPosition position,
        @NonNull final Player player
    ) {
        if (shouldCancelExecution(player, position)) return true;
        updatePosition(player, position);
        return false;
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
