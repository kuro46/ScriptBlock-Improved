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
    public ValidationResult validateCondition(@NonNull final PlayerMoveEvent event) {
        return ValidationResult.valid(AdditionalEventData.builder()
            .position(BlockPosition.fromLocation(event.getPlayer().getLocation()))
            .player(event.getPlayer())
            .build());
    }

    @Override
    public boolean shouldSuppress(
        @NonNull final PlayerMoveEvent event,
        @NonNull final AdditionalEventData additionalData
    ) {
        final Player player = additionalData.getPlayer();
        final BlockPosition position = additionalData.getPosition();
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
