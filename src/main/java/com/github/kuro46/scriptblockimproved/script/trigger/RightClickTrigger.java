package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class RightClickTrigger extends Trigger<PlayerInteractEvent> {

    private static final long COOLDOWN_MS = 300;

    private final Map<Player, Long> lastExecutedTimes = new WeakHashMap<>();

    private RightClickTrigger() {
        super(
            ScriptBlockImproved.getInstance().getPlugin(),
            PlayerInteractEvent.class,
            "rclick"
        );
    }

    public static void register() {
        new RightClickTrigger();
    }

    @Override
    public ValidationResult validateCondition(@NonNull final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return ValidationResult.invalid();
        }
        return ValidationResult.valid(AdditionalEventData.builder()
            .position(BlockPosition.fromBlock(event.getClickedBlock()))
            .player(event.getPlayer())
            .build());
    }

    @Override
    public boolean shouldSuppress(
        @NonNull final PlayerInteractEvent event,
        @NonNull final AdditionalEventData additionalData
    ) {
        final Player player = additionalData.getPlayer();
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
