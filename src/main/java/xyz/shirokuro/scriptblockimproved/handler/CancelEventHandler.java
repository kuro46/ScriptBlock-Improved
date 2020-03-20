package xyz.shirokuro.scriptblockimproved.handler;

import xyz.shirokuro.scriptblockimproved.TriggerData;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class CancelEventHandler implements OptionHandler {

    @Override
    public void onSuppressed(TriggerData triggerData, Player player, List<String> args) {
        boolean cancel = true;
        if (args.size() > 0) {
            cancel = Boolean.parseBoolean(args.get(0));
        }
        if (triggerData.getEvent().isPresent()) {
            final Event event = triggerData.getEvent().get();
            if (!(event instanceof Cancellable)) {
                throw new IllegalStateException(event.getClass() + " does not implements Cancellable");
            }
            ((Cancellable) event).setCancelled(cancel);
        }
    }

    @Override
    public void handleOption(TriggerData triggerData, Player player, List<String> args) {
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return ValidationResult.VALID;
    }
}
