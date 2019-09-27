package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class PressTrigger implements Trigger {

    @Override
    public TriggerName getName() {
        return TriggerName.of("press");
    }

    @Override
    public Class<? extends Event> getTarget() {
        return PlayerInteractEvent.class;
    }

    @Override
    public EventValidateResult validate(final Event event) {
        final PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
        final Player player = interactEvent.getPlayer();

        if (interactEvent.getAction() == Action.PHYSICAL) {
            return EventValidateResult.valid(new EventData(
                BlockPosition.fromLocation(interactEvent.getClickedBlock().getLocation()),
                player));
        } else {
            return EventValidateResult.invalid();
        }
    }
}
