package xyz.shirokuro.scriptblockimproved.handler;

import xyz.shirokuro.scriptblockimproved.TriggerData;
import xyz.shirokuro.scriptblockimproved.common.Utils;
import java.util.List;
import org.bukkit.entity.Player;

public class CommandHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerData triggerData, Player player, List<String> args) {
        player.performCommand(Utils.removeSlashIfNeeded(String.join(" ", args)));
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return ValidationResult.VALID;
    }
}
