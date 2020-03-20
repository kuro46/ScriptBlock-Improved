package xyz.shirokuro.scriptblockimproved.handler;

import xyz.shirokuro.scriptblockimproved.TriggerData;
import java.util.List;
import org.bukkit.entity.Player;

public interface OptionHandler {

    @SuppressWarnings("unused")
    default void onSuppressed(TriggerData triggerData, Player player, List<String> args) {
        // no-op
    }

    void handleOption(TriggerData triggerData, Player player, List<String> args);

    ValidationResult validateArgs(List<String> args);
}
