package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import lombok.NonNull;
import org.bukkit.entity.Player;
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
    public boolean validateCondition(@NonNull final PlayerInteractEvent event) {
        return true;
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
        return false;
    }
}
