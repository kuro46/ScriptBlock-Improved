package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PressTrigger extends Trigger<PlayerInteractEvent> {

    private static final long COOLDOWN_MS = 300;

    private final Map<Player, Long> lastExecutedTimes = new WeakHashMap<>();

    private PressTrigger() {
        super(
            ScriptBlockImproved.getInstance().getPlugin(),
            PlayerInteractEvent.class,
            "press"
        );
    }

    public static void register() {
        new PressTrigger();
    }

    @Override
    public boolean validateCondition(@NonNull final PlayerInteractEvent event) {
        return event.getAction() == Action.PHYSICAL;
    }

    @Override
    public BlockPosition retrievePosition(@NonNull final PlayerInteractEvent event) {
        return BlockPosition.fromBlock(event.getClickedBlock());
    }

    @Override
    public Player retrievePlayer(@NonNull final PlayerInteractEvent event) {
        return event.getPlayer();
    }

    @Override
    public boolean shouldSuppress(
        @NonNull final PlayerInteractEvent event,
        @NonNull final BlockPosition position,
        @NonNull final Player player
    ) {
        if (shouldCancelExecution(player)) return true;
        updateExecutionTime(player);
        return false;
    }

    private boolean shouldCancelExecution(@NonNull final Player player) {
        final long lastExecutedTime = lastExecutedTimes.getOrDefault(player, -1l);
        return lastExecutedTime > (System.currentTimeMillis() - COOLDOWN_MS);
    }

    private void updateExecutionTime(@NonNull final Player player) {
        lastExecutedTimes.put(player, System.currentTimeMillis());
    }
}
