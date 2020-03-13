package com.github.kuro46.scriptblockimproved.handler;

import com.github.kuro46.scriptblockimproved.TriggerInfo;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class CancelEventHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerInfo triggerInfo, Player player, ImmutableList<String> args) {
        if (triggerInfo.getEvent().isPresent()) {
            final Event event = triggerInfo.getEvent().get();
            if (!(event instanceof Cancellable)) {
                throw new IllegalStateException(event.getClass() + " does not implements Cancellable");
            }
            ((Cancellable) event).setCancelled(true);
        }
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return ValidationResult.VALID;
    }
}
