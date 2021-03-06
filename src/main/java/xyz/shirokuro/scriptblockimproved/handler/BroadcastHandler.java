package xyz.shirokuro.scriptblockimproved.handler;

import xyz.shirokuro.scriptblockimproved.TriggerData;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BroadcastHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerData triggerData, Player player, List<String> args) {
        Bukkit.broadcastMessage(String.join(" ", args));
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return ValidationResult.VALID;
    }
}
