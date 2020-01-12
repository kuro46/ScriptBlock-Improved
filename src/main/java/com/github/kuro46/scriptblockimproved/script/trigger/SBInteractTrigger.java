package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import lombok.NonNull;
import org.bukkit.event.player.PlayerInteractEvent;

public final class SBInteractTrigger extends Trigger<PlayerInteractEvent> {

    private SBInteractTrigger() {
        super(
            ScriptBlockImproved.getInstance().getPlugin(),
            PlayerInteractEvent.class,
            "sbinteract"
        );
    }

    public static void register() {
        new SBInteractTrigger();
    }

    @Override
    public ValidationResult validateCondition(@NonNull final PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return ValidationResult.invalid();
        } else {
            return ValidationResult.valid(AdditionalEventData.builder()
                .position(BlockPosition.fromBlock(event.getClickedBlock()))
                .player(event.getPlayer())
                .build());
        }
    }

    @Override
    public boolean shouldSuppress(
        @NonNull final PlayerInteractEvent event,
        @NonNull final AdditionalEventData additionalData
    ) {
        return false;
    }
}
