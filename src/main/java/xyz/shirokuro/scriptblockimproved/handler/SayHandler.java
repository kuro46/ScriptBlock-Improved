package xyz.shirokuro.scriptblockimproved.handler;

import xyz.shirokuro.scriptblockimproved.TriggerData;
import java.util.List;
import org.bukkit.entity.Player;

public final class SayHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerData triggerData, Player player, List<String> args) {
        player.sendMessage(String.join(" ", args));
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return ValidationResult.VALID;
    }
}
